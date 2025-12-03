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
	var llmResp model.LLMResp
	if err := json.Unmarshal(body, &llmResp); err != nil {
		fmt.Printf("解析json失败%v\n", err)
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}

	// 转换数据结构
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
	story.Title = llmResp.Title

	// 事务写入数据库
	tx := db.DB.Begin()
	if err := tx.Create(&shots).Error; err != nil {
		tx.Rollback()
		db.DB.Model(&story).Update("status", model.StatusFailed)
		return
	}
	if err := tx.Model(&story).Update("status", model.StatusCompleted).Error; err != nil {
		tx.Rollback()
		return
	}
	if err := tx.Model(&story).Update("title", story.Title).Error; err != nil {
		tx.Rollback()
		return
	}
	tx.Commit()
	fmt.Print("分镜生成完毕")
}
