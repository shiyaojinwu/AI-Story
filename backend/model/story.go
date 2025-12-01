package model

import "time"

type Story struct {
	ID        uint      `json:"id" gorm:"primaryKey"`
	Title     string    `json:"title"`
	Content   string    `json:"content"`
	Style     string    `json:"style"`
	Status    string    `json:"status" gorm:"default:'pending'"`
	CreatedAt time.Time `json:"createdAt"`
}

func (Story) TableName() string {
	return "stories"
}
