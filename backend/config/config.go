package config

import (
	"log"
	"os"
	"strconv"
)

type Config struct {
	Port int

	//database cconfig
	DBHost     string
	DBPort     int
	DBName     string
	DBUser     string
	DBPassword string
	//首先肯定是host跑在哪 然后是密码 开放的端口之类
}

func LoadConfig() *Config {
	//创建空对象
	cfg := &Config{}
	//加载端口，端口转换
	PortStr := os.Getenv("PORT")
	//先判空
	if PortStr == "" {
		PortStr = "8080"
	}
	//再判数据合理性
	port, err := strconv.Atoi(PortStr)
	if err != nil {
		log.Fatalf("invalid port in .env fille :%v", err)
	}
	cfg.Port = port

	//加载数据库配置
	cfg.DBHost = os.Getenv("DB_HOST")
	cfg.DBUser = os.Getenv("DB_USER")
	cfg.DBName = os.Getenv("DB_NAME")
	cfg.DBPassword = os.Getenv("DB_PASSWORD")

	//数据库端口处理
	DbPortStr := os.Getenv("DB_PORT")
	//先判空
	if DbPortStr == "" {
		DbPortStr = "5432"
	}
	//再判数据合理性
	dbport, err := strconv.Atoi(DbPortStr)
	if err != nil {
		log.Fatalf("invalid dbport in .env fille :%v", err)
	}
	cfg.DBPort = dbport

	return cfg
}
