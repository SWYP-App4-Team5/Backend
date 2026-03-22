package com.jjanpot.server.domain.category.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {
	FOOD_DELIVERY("외식/배달"),
	CAFE_DESSERT("카페/디저트"),
	TRANSPORT("교통"),
	FASHION_BEAUTY("패션/뷰티"),
	HOBBY_CULTURE("취미/문화"),
	ALCOHOL_ENTERTAINMENT("술/유흥"),
	OTHER("기타");

	private final String displayName;
}