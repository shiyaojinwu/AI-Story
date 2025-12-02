package controller

import (
	"net/http"
	"time"

	"story-video-backend/db"
	"story-video-backend/model"

	"github.com/gin-gonic/gin"
)

type CreateStoryRequest struct {
	Content string `json:"content"`
	Style   string `json:"style"`
}

func CreateStory(c *gin.Context) {
	var req CreateStoryRequest

	// 校验是否符合json标签
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":  400,
			"error": "参数错误" + err.Error(),
			"data":  nil,
		})
		return
	}

	// 把请求里的的json映射到model层的story模型
	story := model.Story{
		Content:   req.Content,
		Style:     req.Style,
		Status:    "pending",
		CreatedAt: time.Now(),
	}

	// 然后把这个story模型写入数据库
	if err := db.DB.Create(&story).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "创建故事失败" + err.Error(),
			"data":    nil,
		})
		return
	}

	mockTitle := "This is mock title"

	// 返回结果
	c.JSON(http.StatusOK, gin.H{
		"code":   200,
		"messge": "success",
		"data": gin.H{
			"storyId":   story.ID,
			"status":    model.StatusCompleted,
			"createdAt": story.CreatedAt,
			"title":     mockTitle,
		},
	})
}

func GetStoryDetail(c *gin.Context) {
	id := c.Param("id")
	var story model.Story

	// 根据id查询
	if err := db.DB.First(&story, "id = ?", id).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"code":    404,
			"message": "分镜不存在" + err.Error(),
			"data":    nil,
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "success",
		"data":    story,
	})
}
