package com.example.feishucontentharvester.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {
        private Long id;//编号
        private String updateLog; //更新日志
        private String summary; //文章摘要
        private String link; //文章链接
        private String tags; //文章关键字
}
