package com.jjanpot.server.domain.category.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {
	CAFE_DESSERT("카페/디저트"),
	FOOD_DELIVERY("배달/외식"),
	TRANSPORT("교통/자동차"),
	HOBBY_CULTURE("취미/여가"),
	ALCOHOL_ENTERTAINMENT("술/유흥"),
	SHOPPING("쇼핑"),
	OTHER("기타");

	private final String displayName;
}