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
