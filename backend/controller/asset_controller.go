package controller

import (
	"net/http"
	"time"

	"story-video-backend/db"
	"story-video-backend/model"

	"github.com/gin-gonic/gin"
)

func GetAllAssets(c *gin.Context) {
	var assets []model.Asset

	db.DB.Order("created_at desc").Find(&assets)
	if len(assets) == 0 {
		mockAssets := []model.Asset{
			{
				StoryID: 1, Title: "赛博朋克猫的冒险", Status: "completed", Duration: 15,
				ThumbnailURL: "https://via.placeholder.com/300x169.png?text=CyberPunk",
				VideoURL:     "https://www.w3schools.com/html/mov_bbb.mp4", // 这是一个公网可用的测试视频
				CreatedAt:    time.Now(),
			},
			{
				StoryID: 2, Title: "雨中的黑客", Status: "generating", Duration: 0,
				ThumbnailURL: "https://via.placeholder.com/300x169.png?text=Hacker",
				VideoURL:     "",
				CreatedAt:    time.Now().Add(-10 * time.Minute),
			},
		}
		db.DB.Create(&mockAssets)
		assets = mockAssets
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "success",
		"data":    assets,
	})
}
