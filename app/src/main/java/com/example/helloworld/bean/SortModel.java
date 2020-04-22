package com.example.helloworld.bean;


import me.yokeyword.indexablerv.IndexableEntity;

public class SortModel implements IndexableEntity {

	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private String modeList;
	private String brand_id;
	private String pinyin;

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getModeList() {
		return modeList;
	}

	public void setModeList(String modeList) {
		this.modeList = modeList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}

	@Override
	public String getFieldIndexBy() {
		return name;
	}

	@Override
	public void setFieldIndexBy(String indexField) {
		this.name = indexField;
	}

	@Override
	public void setFieldPinyinIndexBy(String pinyin) {
		this.pinyin = pinyin;
	}
}