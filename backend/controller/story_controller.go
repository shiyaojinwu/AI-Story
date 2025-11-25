package controller

import (
	"net/http"
	"story-video-backend/db"
	"story-video-backend/model"
	"time"

	"github.com/gin-gonic/gin"
)

type CreateStoryRequest struct {
	Content string `json:"content"`
	Style   string `json:"style"`
}

func CreateStory(c *gin.Context) {
	var req CreateStoryRequest

	//校验是否符合json标签
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":  400,
			"error": "参数错误" + err.Error(),
			"data":  nil,
		})
		return
	}

	//把请求里的的json映射到model层的story模型
	story := model.Story{
		Content:   req.Content,
		Style:     req.Style,
		Status:    0,
		CreatedAt: time.Now(),
	}

	//然后把这个story模型写入数据库
	if err := db.DB.Create(&story).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "创建故事失败" + err.Error(),
			"data":    nil,
		})
		return
	}

	//返回结果
	c.JSON(http.StatusOK, gin.H{
		"code":   200,
		"messge": "success",
		"data": gin.H{
			"storyId":   story.ID,
			"status":    story.Status,
			"createdAt": story.CreatedAt,
		},
	})
}
