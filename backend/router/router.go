package router

import (
	"story-video-backend/controller"

	"github.com/gin-gonic/gin"
)

func InitRouter() *gin.Engine {
	r := gin.Default()

	//api分组
	api := r.Group("/api")
	{
		api.POST("/story", controller.CreateStory)
	}

	return r
}
