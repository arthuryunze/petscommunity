package com.chad.baserecyclerviewadapterhelper.adapter;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.baserecyclerviewadapterhelper.R;
import com.chad.baserecyclerviewadapterhelper.entity.Status;
import com.chad.baserecyclerviewadapterhelper.util.SpannableStringUtils;
import com.chad.baserecyclerviewadapterhelper.util.ToastUtils;
import com.chad.baserecyclerviewadapterhelper.util.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名: PullToRefreshAdapter
 * 创 建 人: Allen
 * 创建日期: 16/12/24 19:55
 * 邮   箱: AllenCoder@126.com
 * 修改时间：
 * 修改备注：
 */
public class PullToRefreshAdapter extends BaseQuickAdapter<Status, BaseViewHolder> {
    private List<String> strings;
    private List<String> ids;
    private List<String> texts;
    private List<String> urls;

    public PullToRefreshAdapter() {
        super(R.layout.layout_animation, null);
    }

    public PullToRefreshAdapter(List<String> strings) {
        super(R.layout.layout_animation, null);
        this.strings = strings;

	    getMessage();

    }
    public void getMessage(){
        ids = new ArrayList<String>();
        texts = new ArrayList<String>();
        urls = new ArrayList<String>();

        for (String string : strings) {
            ids.add(string.substring(0, string.indexOf("|")));
            texts.add(string.substring(string.indexOf("|") + 1, string
                    .lastIndexOf("|")));
            urls.add(string.substring(string.lastIndexOf("|") + 1));
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Status item) {
        int index=0;
        while(index<ids.size())
        {
            if(helper.getLayoutPosition()==index){
                helper.setText(R.id.tweetName, texts.get(index));
                Glide.with(mContext).load(urls.get(index)).into((ImageView)helper.getView(R.id.img));
                ((TextView) helper.getView(R.id.tweetText)).setText(SpannableStringUtils.getBuilder("this is the"+index+" label.").append("dog label").setClickSpan(clickableSpan).create());
            }
            index++;

        }


//        switch (helper.getLayoutPosition() % 3) {
//            case 0:
//                helper.setText(R.id.tweetName, texts.get(0));
//                Glide.with(mContext).load(urls.get(0)).into((ImageView)helper.getView(R.id.img));
//                ((TextView) helper.getView(R.id.tweetText)).setText(SpannableStringUtils.getBuilder("this is a dog label.").append("dog label").setClickSpan(clickableSpan).create());
////              helper.setImageResource(R.id.img, R.mipmap.animation_img1);
//                break;
//            case 1:
//                helper.setText(R.id.tweetName, texts.get(1));
//                Glide.with(mContext).load(urls.get(1)).into((ImageView)helper.getView(R.id.img));
//
//                ((TextView) helper.getView(R.id.tweetText)).setText(SpannableStringUtils.getBuilder("this is a cat label.").create());
////                helper.setText(R.id.tweetName, "this is server image text.");
////                Glide.with(mContext).load("http://148.70.41.175:8080/MyApp/res/image/dog-1.jpg").into((ImageView)helper.getView(R.id.img));
//                break;
//            case 2:
//                helper.setText(R.id.tweetName, "text");
//                helper.setImageResource(R.id.img, R.mipmap.animation_img3);
//                break;
//            default:
//                break;
//        }


//        switch (helper.getLayoutPosition() % 3) {
//            case 0:
//                helper.setImageResource(R.id.img, R.mipmap.animation_img1);
//                helper.getView(R.id.img);
//                break;
//            case 1:
//                helper.setImageResource(R.id.img, R.mipmap.animation_img2);
//                break;
//            case 2:
//                helper.setImageResource(R.id.img, R.mipmap.animation_img3);
//                break;
//            default:
//                break;
//        }

//        helper.setText(R.id.tweetName, "Hoteis in Rio de Janeiro");
//        String msg = "\"He was one of Australia's most of distinguished artistes, renowned for his portraits\"";
//        ((TextView) helper.getView(R.id.tweetText)).setText(SpannableStringUtils.getBuilder(msg).append("landscapes and nedes").setClickSpan(clickableSpan).create());
        ((TextView) helper.getView(R.id.tweetText)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            ToastUtils.showShortToast("事件触发了 landscapes and nedes");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Utils.getContext().getResources().getColor(R.color.clickspan_color));
            ds.setUnderlineText(true);
        }
    };


}
