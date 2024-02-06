package com.devo.bim.component;

import java.text.DecimalFormat;

public class NumberToKorean {
    private static final String[] KOREAN_NUMBERS = {"영", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구"};
    private static final String[] KOREAN_UNITS = {"", "십", "백", "천"};
    private static final String[] KOREAN_GROUP_UNITS = {"", "만", "억", "조", "경"};

    public static String convertToKorean(long number) {
        if (number == 0) {
            return KOREAN_NUMBERS[0];
        }

        StringBuilder sb = new StringBuilder();
        int groupIndex = 0;
        while (number > 0) {
            int group = (int) (number % 10000);
            sb.insert(0, convertGroupToKorean(group) + KOREAN_GROUP_UNITS[groupIndex]);
            number /= 10000;
            groupIndex++;
        }

        return sb.toString();
    }

    private static String convertGroupToKorean(int group) {
        StringBuilder sb = new StringBuilder();
        int unitIndex = 0;
        while (group > 0) {
            int num = group % 10;
            if (num > 0) {
                sb.insert(0, KOREAN_NUMBERS[num] + KOREAN_UNITS[unitIndex]);
            }
            unitIndex++;
            group /= 10;
        }
        return sb.toString();
    }

    public static String amountToKorean(long amount) {
        String koreanAmount = convertToKorean(amount);
        DecimalFormat df = new DecimalFormat("#,##0");
        String formattedAmount = df.format(amount);
        return formattedAmount + " => " + koreanAmount;
    }

    public static void main(String[] args) {
        long amount = 123456789012345L;
        String koreanAmount = amountToKorean(amount);
        System.out.println(koreanAmount);
    }
}
