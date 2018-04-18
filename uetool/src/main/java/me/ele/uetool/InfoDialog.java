package me.ele.uetool;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import me.ele.uetool.items.AddMinusEditItem;
import me.ele.uetool.items.EditTextItem;
import me.ele.uetool.items.Item;
import me.ele.uetool.items.SwitchItem;
import me.ele.uetool.items.TextItem;
import me.ele.uetool.items.TitleItem;

import static me.ele.uetool.InfoDialog.Adapter.ViewType.TYPE_ADD_MINUS_EDIT;
import static me.ele.uetool.InfoDialog.Adapter.ViewType.TYPE_EDIT_TEXT;
import static me.ele.uetool.InfoDialog.Adapter.ViewType.TYPE_SWITCH;
import static me.ele.uetool.InfoDialog.Adapter.ViewType.TYPE_TEXT;
import static me.ele.uetool.InfoDialog.Adapter.ViewType.TYPE_TITLE;

public class InfoDialog extends Dialog {

  private RecyclerView vList;
  private Adapter adapter = new Adapter();

  public InfoDialog(Context context) {
    super(context, R.style.uet_Theme_Holo_Dialog_background_Translecent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.uet_info_layout);
    vList = findViewById(R.id.list);
    vList.setAdapter(adapter);
    vList.setLayoutManager(new LinearLayoutManager(getContext()));
  }

  public void show(Element element) {
    adapter.notifyNoShopsDataSetChanged(element);
    show();
    Window dialogWindow = getWindow();
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
    lp.x = element.getRect().left;
    lp.y = element.getRect().bottom;
    lp.height = Util.getScreenHeight(getContext()) / 2;
    dialogWindow.setAttributes(lp);
  }

  @Override public void dismiss() {
    super.dismiss();
    adapter.clear();
  }

  public static class Adapter extends RecyclerView.Adapter {

    private List<Item> items = new ArrayList<>();

    public void notifyNoShopsDataSetChanged(Element element) {
      items.clear();
      items.addAll(element.getAttrs());
      notifyDataSetChanged();
    }

    public void clear() {
      items.clear();
      notifyDataSetChanged();
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      switch (viewType) {
        case TYPE_TITLE:
          return TitleViewHolder.newInstance(parent);
        case TYPE_TEXT:
          return TextViewHolder.newInstance(parent);
        case TYPE_EDIT_TEXT:
          return EditTextViewHolder.newInstance(parent);
        case TYPE_SWITCH:
          return SwitchViewHolder.newInstance(parent);
        case TYPE_ADD_MINUS_EDIT:
          return AddMinusEditViewHolder.newInstance(parent);
      }
      return null;
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      if (holder.getClass() == TitleViewHolder.class) {
        ((TitleViewHolder) holder).bindView((TitleItem) getItem(position));
      } else if (holder.getClass() == TextViewHolder.class) {
        ((TextViewHolder) holder).bindView((TextItem) getItem(position));
      } else if (holder.getClass() == EditTextViewHolder.class) {
        ((EditTextViewHolder) holder).bindView((EditTextItem) getItem(position));
      } else if (holder.getClass() == SwitchViewHolder.class) {
        ((SwitchViewHolder) holder).bindView((SwitchItem) getItem(position));
      } else if (holder.getClass() == AddMinusEditViewHolder.class) {
        ((AddMinusEditViewHolder) holder).bindView((AddMinusEditItem) getItem(position));
      }
    }

    @Override public int getItemViewType(int position) {
      Item item = getItem(position);
      if (item.getClass() == TitleItem.class) {
        return TYPE_TITLE;
      } else if (item.getClass() == TextItem.class) {
        return TYPE_TEXT;
      } else if (item.getClass() == EditTextItem.class) {
        return TYPE_EDIT_TEXT;
      } else if (item.getClass() == SwitchItem.class) {
        return TYPE_SWITCH;
      } else if (item.getClass() == AddMinusEditItem.class) {
        return TYPE_ADD_MINUS_EDIT;
      }
      throw new RuntimeException("Unknown item type.");
    }

    @Override public int getItemCount() {
      return items.size();
    }

    @Nullable @SuppressWarnings("unchecked")
    protected <T extends Item> T getItem(int adapterPosition) {
      if (adapterPosition < 0 || adapterPosition >= items.size()) {
        return null;
      }
      return (T) items.get(adapterPosition);
    }

    @IntDef({
        TYPE_TITLE,
        TYPE_TEXT,
        TYPE_EDIT_TEXT,
        TYPE_SWITCH,
        TYPE_ADD_MINUS_EDIT,
    })
    @Retention(RetentionPolicy.SOURCE) @interface ViewType {
      int TYPE_TITLE = 1;
      int TYPE_TEXT = 2;
      int TYPE_EDIT_TEXT = 3;
      int TYPE_SWITCH = 4;
      int TYPE_ADD_MINUS_EDIT = 5;
    }

    public static abstract class BaseViewHolder<T extends Item> extends RecyclerView.ViewHolder {

      protected T item;

      public BaseViewHolder(View itemView) {
        super(itemView);
      }

      public void bindView(T t) {
        item = t;
      }
    }

    public static class TitleViewHolder extends BaseViewHolder<TitleItem> {

      private TextView vTitle;

      public TitleViewHolder(View itemView) {
        super(itemView);
        vTitle = itemView.findViewById(R.id.title);
      }

      public static TitleViewHolder newInstance(ViewGroup parent) {
        return new TitleViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.uet_cell_title, parent, false));
      }

      @Override public void bindView(TitleItem titleItem) {
        super.bindView(titleItem);
        vTitle.setText(titleItem.getTitle());
      }
    }

    public static class TextViewHolder extends BaseViewHolder<TextItem> {

      private TextView vName;
      private TextView vDetail;

      public TextViewHolder(View itemView) {
        super(itemView);
        vName = itemView.findViewById(R.id.name);
        vDetail = itemView.findViewById(R.id.detail);
      }

      public static TextViewHolder newInstance(ViewGroup parent) {
        return new TextViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.uet_cell_text, parent, false));
      }

      @Override public void bindView(final TextItem textItem) {
        super.bindView(textItem);
        vName.setText(textItem.getName());
        vDetail.setText(textItem.getDetail());
        vDetail.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (textItem.isEnableCopy()) {
              Util.clipText(v.getContext(), textItem.getDetail());
            }
          }
        });
      }
    }

    public static class EditTextViewHolder<T extends EditTextItem>
        extends BaseViewHolder<T> {

      protected TextView vName;
      protected EditText vDetail;
      @Nullable private View vColor;

      protected TextWatcher textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
          try {
            if (item.getType() == EditTextItem.Type.TYPE_TEXT) {
              TextView textView = ((TextView) (item.getElement().getView()));
              if (!TextUtils.equals(textView.getText().toString(), s.toString())) {
                textView.setText(s.toString());
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_TEXT_SIZE) {
              TextView textView = ((TextView) (item.getElement().getView()));
              float textSize = Float.valueOf(s.toString());
              if (textView.getTextSize() != textSize) {
                textView.setTextSize(textSize);
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_TEXT_COLOR) {
              TextView textView = ((TextView) (item.getElement().getView()));
              int color = Color.parseColor(vDetail.getText().toString());
              if (color != textView.getCurrentTextColor()) {
                vColor.setBackgroundColor(color);
                textView.setTextColor(color);
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_WIDTH) {
              View view = item.getElement().getView();
              int width = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(width - view.getWidth()) >= Util.dip2px(itemView.getContext(), 1)) {
                view.getLayoutParams().width = width;
                view.requestLayout();
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_HEIGHT) {
              View view = item.getElement().getView();
              int height = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(height - view.getHeight()) >= Util.dip2px(itemView.getContext(), 1)) {
                view.getLayoutParams().height = height;
                view.requestLayout();
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_LEFT) {
              View view = item.getElement().getView();
              int paddingLeft = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(paddingLeft - view.getPaddingLeft()) >= Util.dip2px(
                  itemView.getContext(), 1)) {
                view.setPadding(paddingLeft, view.getPaddingTop(), view.getPaddingRight(),
                    view.getPaddingBottom());
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_RIGHT) {
              View view = item.getElement().getView();
              int paddingRight = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(paddingRight - view.getPaddingRight()) >= Util.dip2px(
                  itemView.getContext(), 1)) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), paddingRight,
                    view.getPaddingBottom());
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_TOP) {
              View view = item.getElement().getView();
              int paddingTop = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(paddingTop - view.getPaddingTop()) >= Util.dip2px(
                  itemView.getContext(), 1)) {
                view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(),
                    view.getPaddingBottom());
              }
            } else if (item.getType() == EditTextItem.Type.TYPE_PADDING_BOTTOM) {
              View view = item.getElement().getView();
              int paddingBottom = Util.dip2px(itemView.getContext(), Integer.valueOf(s.toString()));
              if (Math.abs(paddingBottom - view.getPaddingBottom()) >= Util.dip2px(
                  itemView.getContext(), 1)) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                    paddingBottom);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        @Override public void afterTextChanged(Editable s) {

        }
      };

      public EditTextViewHolder(View itemView) {
        super(itemView);
        vName = itemView.findViewById(R.id.name);
        vDetail = itemView.findViewById(R.id.detail);
        vColor = itemView.findViewById(R.id.color);
        vDetail.addTextChangedListener(textWatcher);
      }

      public static EditTextViewHolder newInstance(ViewGroup parent) {
        return new EditTextViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.uet_cell_edit_text, parent, false));
      }

      @Override public void bindView(final T editTextItem) {
        super.bindView(editTextItem);
        vName.setText(editTextItem.getName());
        vDetail.setText(editTextItem.getDetail());
        if (vColor != null) {
          try {
            vColor.setBackgroundColor(Color.parseColor(editTextItem.getDetail()));
            vColor.setVisibility(View.VISIBLE);
          } catch (Exception e) {
            vColor.setVisibility(View.GONE);
          }
        }
      }
    }

    public static class AddMinusEditViewHolder extends EditTextViewHolder<AddMinusEditItem> {

      private View vAdd;
      private View vMinus;

      public AddMinusEditViewHolder(View itemView) {
        super(itemView);
        vAdd = itemView.findViewById(R.id.add);
        vMinus = itemView.findViewById(R.id.minus);
        vAdd.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            try {
              int textSize = Integer.valueOf(vDetail.getText().toString());
              vDetail.setText(String.valueOf(++textSize));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
        vMinus.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            try {
              int textSize = Integer.valueOf(vDetail.getText().toString());
              if (textSize > 0) {
                vDetail.setText(String.valueOf(--textSize));
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
      }

      public static AddMinusEditViewHolder newInstance(ViewGroup parent) {
        return new AddMinusEditViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uet_cell_add_minus_edit, parent, false));
      }

      @Override public void bindView(AddMinusEditItem editTextItem) {
        super.bindView(editTextItem);
      }
    }

    public static class SwitchViewHolder extends BaseViewHolder<SwitchItem> {

      private TextView vName;
      private SwitchCompat vSwitch;

      public SwitchViewHolder(View itemView) {
        super(itemView);

        vName = itemView.findViewById(R.id.name);
        vSwitch = itemView.findViewById(R.id.switch_view);
        vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
              if (item.getElement().getView() instanceof TextView) {
                TextView textView = ((TextView) (item.getElement().getView()));
                if (item.getType() == SwitchItem.Type.TYPE_IS_BOLD) {
                  textView.setTypeface(null, isChecked ? Typeface.BOLD : Typeface.NORMAL);
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
      }

      public static SwitchViewHolder newInstance(ViewGroup parent) {
        return new SwitchViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.uet_cell_switch, parent, false));
      }

      @Override public void bindView(SwitchItem switchItem) {
        super.bindView(switchItem);

        vName.setText(switchItem.getName());
        vSwitch.setChecked(switchItem.isChecked());
      }
    }
  }
}

