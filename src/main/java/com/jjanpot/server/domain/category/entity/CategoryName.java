package com.jjanpot.server.domain.category.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryName {
	FOOD_DELIVERY("배달/외식"),
	CAFE_DESSERT("카페/디저트"),
	TRANSPORT("교통/자동차"),
	FASHION_BEAUTY("패션/뷰티"),
	HOBBY_CULTURE("취미/여가"),
	ALCOHOL("술/유흥"),
	OTHER("기타");

	private final String displayName;
}