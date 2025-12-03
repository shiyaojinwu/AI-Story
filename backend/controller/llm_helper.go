package controller

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"

	"story-video-backend/db"
	"story-video-backend/model"
)

func processLLMGeneration(story model.Story) {
	llmReq := model.LLMReq{Story: story.Content}
	jsonData, _ := json.Marshal(llmReq)

	// 发起请求，调用失败就更新状态为失败
	resp, err := http.Post(model.LLMUrl, "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		fmt.Printf("调用llm失败：%v\n", err)
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}

	defer resp.Body.Close()
	body, _ := io.ReadAll(resp.Body)
	fmt.Printf("llm接口原始响应%d，%s", resp.StatusCode, string(body))
	var llmResp model.LLMResp
	if err := json.Unmarshal(body, &llmResp); err != nil {
		fmt.Printf("解析json失败%v\n,故事%d", err, story.ID)
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}

	// 转换数据结构
	var createdShots []model.Shot // 这个变量用来发起协程生成图片
	var shots []model.Shot
	for _, item := range llmResp.Shots {
		shots = append(shots, model.Shot{
			StoryID:    story.ID,
			Order:      item.SortOrder,
			Title:      item.Title,
			Prompt:     item.Prompt,
			Narration:  item.Narration,
			Transition: item.Narration,
			Status:     model.StatusGenerating,
		})
	}
	createdShots = append(createdShots, shots...)
	story.Title = llmResp.Title

	// 事务写入数据库
	tx := db.DB.Begin()
	if err := tx.Create(&shots).Error; err != nil {
		tx.Rollback()
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}
	if err := tx.Model(&story).Updates(map[string]interface{}{
		"status": model.StatusCompleted,
		"title":  story.Title,
	}).Error; err != nil {
		tx.Rollback()
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}
	tx.Commit()
	fmt.Print("分镜结构生成完毕,开始并发生成图片%d", story.ID)

	// 协程触发图片生成任务
	for _, s := range createdShots {
		go processImageGeneration(s.ID, s.Prompt)
	}
}

func processImageGeneration(shotID uint, prompt string) {
	reqBody := model.ImageReq{
		Prompt: prompt,
		Size:   "1024x576",
	}
	jsonData, _ := json.Marshal(reqBody)

	// 发送请求
	resp, err := http.Post(model.ImageUrl, "application/json", bytes.NewBuffer(jsonData))
	if err != nil {
		fmt.Printf("shot%d图片生成错误%v", shotID, err)
		db.DB.Model(&model.Shot{}).Where("id = ?", shotID).Update("status", model.StatusFailed)
		return
	}
	defer resp.Body.Close()

	// 读取响应
	bodyBytes, _ := io.ReadAll(resp.Body)

	if resp.StatusCode != http.StatusOK {
		fmt.Printf("shot %d 图片接口报错 %d\n", shotID, resp.StatusCode)
		db.DB.Model(&model.Shot{}).Where("id = ?", shotID).Update("status", model.StatusFailed)
		return
	}

	// 解析并转换
	var imgResp model.ImageResp
	if err := json.Unmarshal(bodyBytes, &imgResp); err != nil {
		fmt.Printf("shot %d 图片json解析失败%v\n", shotID, err)
		db.DB.Model(&model.Shot{}).Where("id = ?", shotID).Update("status", model.StatusFailed)
		return
	}

	db.DB.Model(&model.Shot{}).Where("id = ?", shotID).Updates(map[string]any{
		"image_url": imgResp.ImageURL,
		"status":    model.StatusCompleted,
	})
	fmt.Printf("shot %d 图片生成成功%s\n", shotID, imgResp.ImageURL)
}
