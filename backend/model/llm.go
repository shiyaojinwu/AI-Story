package model

type LLMReq struct {
	Story string `json:"story"`
}

type LLMResp struct {
	Shots []struct {
		SortOrder  int    `json:"sortOrder"`
		Title      string `json:"title"`
		Prompt     string `json:"prompt"`
		Narration  string `json:"narration"`
		Transition string `json:"transition"`
	} `json:"shots"`
	Title string `json:"title"`
}

type ImageReq struct {
	Prompt string `json:"prompt"`
	Size   string `json:"size"`
}

type ImageResp struct {
	ImageURL string `json:"image_url"`
}

type VideoGenReq struct {
	StoryID string         `json:"story_id"`
	Shots   []VideoGenShot `json:"shots"`
}

type VideoGenShot struct {
	ImageURL   string `json:"image_url"`
	Narration  string `json:"narration"`
	Transition string `json:"transition"`
}

type VideoGenResp struct {
	VideoURL string `json:"video_url"`
	Duration string `json:"duration"`
}
