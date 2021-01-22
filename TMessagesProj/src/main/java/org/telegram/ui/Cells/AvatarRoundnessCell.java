package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class AvatarRoundnessCell extends LinearLayout {

    private class ListView extends FrameLayout {

        private RadioButton button;
        private boolean isThreeLines;
        private RectF rect = new RectF();
        private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        private int option = 0;

        public ListView(Context context, int option) {
            super(context);
            setWillNotDraw(false);

            this.option = option;
            textPaint.setTextSize(AndroidUtilities.dp(13));

            button = new RadioButton(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            button.setSize(AndroidUtilities.dp(20));
            addView(button, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 26, 10, 0));
            button.setChecked(option == SharedConfig.dialogCellAvatarType, false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int color = Theme.getColor(Theme.key_switchTrack);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);

            button.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));

            rect.set(AndroidUtilities.dp(1), AndroidUtilities.dp(1), getMeasuredWidth() - AndroidUtilities.dp(1), AndroidUtilities.dp(73));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (43 * button.getProgress()), r, g, b));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.chat_instantViewRectPaint);

            rect.set(0, 0, getMeasuredWidth(), AndroidUtilities.dp(74));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (31 * (1.0f - button.getProgress())), r, g, b));
            canvas.drawRoundRect(rect, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Theme.dialogs_onlineCirclePaint);

            String text;
            if(option == 0) {
                text = LocaleController.getString("DialogCellAvatarRadiusSquare", R.string.DialogCellAvatarRadiusSquare);
            } else if(option == 1) {
                text = LocaleController.getString("DialogCellAvatarRadiusRoundSquare", R.string.DialogCellAvatarRadiusRoundSquare);
            } else {
                text = LocaleController.getString("DialogCellAvatarRadiusCircle", R.string.DialogCellAvatarRadiusCircle);
            }

            int width = (int) Math.ceil(textPaint.measureText(text));

            Theme.dialogs_onlineCirclePaint.setColor(Color.argb(90, r, g, b));
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.drawText(text, (getMeasuredWidth() - width) / 2, AndroidUtilities.dp(96), textPaint);

            int avatarSize = 55;
            rect.set(AndroidUtilities.dp(36) - avatarSize, rect.height() / 2 - avatarSize , AndroidUtilities.dp(36) + avatarSize, rect.height() / 2 + avatarSize);

            float radius = 0;
            if(option == 0) {
                radius = 0;
            } else if(option == 1) {
                radius = 30;
            } else {
                radius = 100;
            }

            radius /= 1.5f;
            canvas.drawRoundRect(rect, radius, radius, Theme.dialogs_onlineCirclePaint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(RadioButton.class.getName());
            info.setChecked(button.isChecked());
            info.setCheckable(true);
        }
    }

    private ListView[] listView = new ListView[3];

    public AvatarRoundnessCell(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setPadding(AndroidUtilities.dp(21), AndroidUtilities.dp(10), AndroidUtilities.dp(21), 0);

        for (int a = 0; a < listView.length; a++) {
            listView[a] = new ListView(context, a);
            addView(listView[a], LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 0.5f, a != 0 && a < listView.length ? 10 : 0, 0, 0, 0));
            int finalA = a;
            listView[a].setOnClickListener(v -> {
                for (int b = 0; b < 3; b++) {
                    listView[b].button.setChecked(listView[b] == v, true);
                }
                didSelectAvatarRadiusType(finalA);
            });
        }
    }

    protected void didSelectAvatarRadiusType(int type) {

    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (int a = 0; a < listView.length; a++) {
            listView[a].invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123), View.MeasureSpec.EXACTLY));
    }
}
