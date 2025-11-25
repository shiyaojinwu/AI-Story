package main

import (
	"fmt"
	"log"
	"os"

	"github.com/joho/godotenv"

	"story-video-backend/config"
	"story-video-backend/db"
	"story-video-backend/model"
	"story-video-backend/router"
)

func main() {
	//加载环境变量
	if err := godotenv.Load(); err != nil {
		log.Println("can find file .env")
	}
	//加载配置
	cfg := config.LoadConfig()

	//初始化数据库
	database := db.InitDB(cfg)

	//更新数据表
	err := database.AutoMigrate(&model.Story{}, &model.Shot{}, &model.Asset{})
	if err != nil {
		log.Fatalf("数据库初始化失败：%v", err)
	}
	log.Println("=^w^= 数据库连接成功")

	//初始化路由
	r := router.InitRouter()

	//start the service and print port
	address := fmt.Sprintf(":%d", cfg.Port)
	fmt.Println("-------------------------------------------------")
	fmt.Printf("Ai Story Running on Port %d\n", cfg.Port)
	fmt.Printf("Server started at http://localhost%s\n", address)
	fmt.Println("-------------------------------------------------")

	if err := r.Run(address); err != nil {
		log.Fatalf("启动服务失败: %v", err)
		os.Exit(1)
	}

}
