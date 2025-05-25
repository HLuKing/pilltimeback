package com.pillsolo.api.dto.api;

import lombok.Data;

import java.util.List;

@Data
public class DrugApiResponse {
    private Body body;

    @Data
    public static class Body {
        private List<Item> items;
    }

    @Data
    public static class Item {
        private String itemName;             // 제품명
        private String entpName;             // 제조사
        private String efcyQesitm;           // 효능 효과
        private String itemImage;            // 이미지 URL

        private String useMethodQesitm;      // 복용 방법
        private String atpnWarnQesitm;       // 경고 문구
        private String atpnQesitm;           // 복용 전 주의사항
        private String intrcQesitm;          // 병용 금기/상호작용
        private String seQesitm;             // 부작용
        private String depositMethodQesitm;  // 보관 방법

        private String itemSeq;              // 의약품 고유번호
        private String updateDate;             // 업데이트 날짜
    }
}
