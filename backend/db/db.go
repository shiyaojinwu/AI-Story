package db

import "story-video-backend/config"

type DB struct{}

func InitDB(cfg *config.Config) *DB {
	return &DB{}
}
