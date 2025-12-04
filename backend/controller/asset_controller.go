package controller

import (
	"net/http"
	"strconv"
	"time"

	"story-video-backend/db"
	"story-video-backend/model"

	"github.com/gin-gonic/gin"
)

func GetVideoPreview(c *gin.Context) {
	storyID := c.Param("id")
	var asset model.Asset
	if err := db.DB.Where("story_id = ?", storyID).First(&asset).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"code":    404,
			"message": "找不到成品",
			"data":    nil,
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "找到预览成品了",
		"data":    asset,
	})
}

func GenerateVideo(c *gin.Context) {
	storyIDStr := c.Param("id")
	storyID, _ := strconv.Atoi(storyIDStr)

	// 判断故事存在性
	var story model.Story
	if err := db.DB.First(&story, storyID).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"code":    404,
			"message": "故事不存在",
			"data":    nil,
		})
		return
	}

	// 查出分镜，组装请求体
	var shots []model.Shot
	db.DB.Where("story_id = ?", storyID).Order("sort_order asc").Find(&shots)
	// 必须先让分镜生成
	if len(shots) == 0 {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": "该故事未生成分镜，无法生成视频",
			"data":    nil,
		})
		return
	}

	// 预创建asset
	// 封面用第一张
	coverURL := ""
	if len(shots) > 0 {
		coverURL = shots[0].ImageURL
	}

	asset := model.Asset{
		StoryID:      uint(storyID),
		Title:        story.Title,
		Status:       model.StatusGenerating,
		ThumbnailURL: coverURL,
		CreatedAt:    time.Now(),
	}

	// 先清理旧的
	var oldAsset model.Asset
	if db.DB.Where("story_id = ?", storyID).First(&oldAsset).Error == nil {
		asset.ID = oldAsset.ID
		db.DB.Model(&oldAsset).Updates(map[string]any{
			"status":        model.StatusGenerating,
			"video_url":     "",
			"thumbnail_url": coverURL,
		})
	} else {
		db.DB.Create(&asset)
	}

	// 开始异步生成视频
	go processVideoGeneration(asset.ID, storyIDStr, shots)

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "视频生成任务已启动",
		"data": gin.H{
			"id": strconv.Itoa(int(asset.ID)),
		},
	})
}

func GetAllAssets(c *gin.Context) {
	var assets []model.Asset
	db.DB.Order("created_at desc").Find(&assets)

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "success",
		"data":    assets,
	})
}
