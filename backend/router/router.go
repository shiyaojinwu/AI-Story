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

		//shot模块
		//获取分镜列表
		api.GET("/story/:id/shots", controller.GetShotsByStory)
		//获取分镜详情
		api.GET("/shot/:id", controller.GetShotDetail)
		//更新分镜
		api.POST("/shot/:id/update", controller.UpdateShot)
		//生成mock数据
		api.POST("/story/:id/mock-gen", controller.GenMockShots)
	}

	return r
}
