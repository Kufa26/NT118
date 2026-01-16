package com.example.cashmate.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashmate.R;

public class FAQFragment extends Fragment {

    private final String[] questions = {
            "Làm sao để đăng ký tài khoản CashMate?",
            "Quên mật khẩu thì khôi phục như thế nào?",
            "Có đăng nhập được bằng Google / Facebook không?",
            "Đổi email đăng nhập ở đâu?",
            "Một tài khoản có dùng được trên nhiều thiết bị không?",
            "Cách thêm giao dịch thu / chi?",
            "Có thể sửa hoặc xóa giao dịch đã nhập không?",
            "Giao dịch bị nhập nhầm ngày thì sửa ở đâu?",
            "Có thể tạo danh mục chi tiêu mới không?",
            "Làm sao để tạo ngân sách chi tiêu?",
            "Ngân sách hoạt động theo tháng hay theo ngày?",
            "Khi vượt ngân sách thì app có cảnh báo không?",
            "Có thể tạo nhiều ngân sách cho cùng một danh mục không?",
            "Ngân sách có tự reset khi sang tháng mới không?",
            "Xóa danh mục thì các giao dịch cũ xử lý thế nào?",
            "Làm sao để đổi icon / màu của danh mục?",
            "Phân biệt danh mục thu và danh mục chi như thế nào?",
            "Xem báo cáo chi tiêu theo tháng/năm ở đâu?",
            "Làm sao để biết mình chi nhiều nhất vào khoản nào?"
    };

    private final String[] answers = {
            "Mở ứng dụng → Đăng ký → nhập email hoặc đăng ký bằng Google/Facebook.",
            "Chọn Quên mật khẩu tại màn hình đăng nhập và làm theo hướng dẫn trong email.",
            "Có, ứng dụng hỗ trợ đăng nhập bằng Google và Facebook.",
            "Vào Cài đặt → Tài khoản → Thông tin cá nhân để đổi email.",
            "Có, dữ liệu sẽ được đồng bộ trên nhiều thiết bị.",
            "Nhấn nút + → chọn Thu hoặc Chi → nhập thông tin và lưu.",
            "Có, nhấn vào giao dịch để sửa hoặc xóa.",
            "Mở giao dịch → chọn Sửa → chỉnh lại ngày.",
            "Có, vào Cài đặt → Danh mục → Thêm danh mục.",
            "Vào Ngân sách → Tạo ngân sách → nhập số tiền và danh mục.",
            "Ngân sách hoạt động theo tháng.",
            "Có, ứng dụng sẽ cảnh báo khi vượt ngân sách.",
            "Không, mỗi danh mục chỉ có một ngân sách trong cùng thời gian.",
            "Có, ngân sách sẽ tự reset khi sang tháng mới.",
            "Giao dịch cũ vẫn giữ, bạn cần gán lại danh mục khác.",
            "Vào Cài đặt → Danh mục → chọn danh mục để đổi icon/màu.",
            "Danh mục Thu là tiền vào, Danh mục Chi là tiền ra.",
            "Vào mục Báo cáo → chọn tháng hoặc năm.",
            "Trong Báo cáo, danh mục có số tiền cao nhất là khoản chi nhiều nhất."
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.activity_faq, container, false);

        // BACK TOOLBAR
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack()
            );
        }

        LinearLayout faqContainer = view.findViewById(R.id.faqContainer);
        LayoutInflater itemInflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < questions.length; i++) {
            View item = itemInflater.inflate(R.layout.item_faq, faqContainer, false);

            TextView tvQuestion = item.findViewById(R.id.tvQuestion);
            TextView tvAnswer = item.findViewById(R.id.tvAnswer);
            ImageView imgArrow = item.findViewById(R.id.imgArrow);
            LinearLayout layoutQuestion = item.findViewById(R.id.layoutQuestion);

            if (tvQuestion == null || tvAnswer == null || imgArrow == null || layoutQuestion == null) {
                continue; // chống crash
            }

            tvQuestion.setText((i + 1) + ". " + questions[i]);
            tvAnswer.setText(answers[i]);

            layoutQuestion.setOnClickListener(v -> {
                if (tvAnswer.getVisibility() == View.GONE) {
                    tvAnswer.setVisibility(View.VISIBLE);
                    imgArrow.setRotation(180);
                } else {
                    tvAnswer.setVisibility(View.GONE);
                    imgArrow.setRotation(0);
                }
            });

            faqContainer.addView(item);
        }

        return view;
    }
}
