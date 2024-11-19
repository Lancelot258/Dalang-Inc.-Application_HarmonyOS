/*
 * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.askerov.dynamicgrid;

import com.uestc.dalangapp.ResourceTable;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.MimeData;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.agp.window.service.Display;
import ohos.agp.window.service.DisplayManager;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.media.image.PixelMap;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DragLayout
 *
 * @since 2021-05-15
 */
public class DragGridView extends DirectionalLayout  {
    private static final String UP_GRID_TAG = "upGrid";
    private static final String DOWN_GRID_TAG = "downGrid";
    private static final String ADD_GRID_TAG = "addGrid";

    private static final int INVALID_POSITION = -1;

    private boolean isViewOnExchange;
    private boolean isScroll;
    private GridView parentView;
    private ScrollView scrollView;

    private boolean isBack;

    // Item when dragged
    private int scrollViewTop;
    private int scrollViewLeft;
    private Component selectedView;
    private Component slice;

    private boolean isViewOnDrag=false;

   // private final Map<Component, AnimatorProperty> animatorList = new HashMap<>();
    private int duration = 320;
    private EventHandler eventHandler = new EventHandler(EventRunner.getMainEventRunner());

    private List<GridItemInfo> dataList = new ArrayList<>();

    private float alphaFloat = 0.8f;
    private int animationRotate = 6;
    private int column = 3;

    private int lastPointY;

    private Display display = DisplayManager.getInstance().getDefaultDisplay(getContext()).get();
    private Point point = new Point();
    private int displayHeight;
    private int currentDragY;

    Context context;
    List<Component> listComponent;
    private final int imgRequestCode=1123;
    AbilitySlice maiAbilitySlice=null;
    GridAdapter adapter;
    int curIdx=-1;
    int newIdx=-1;
    boolean bMoveTouch=false;
    Component shadowComponent;

    public void SetFatherAbility(AbilitySlice ability)
    {
        maiAbilitySlice=ability;
    }



    private int getIdxByCommponet(Component component)
    {
        int i,n;
        n=listComponent.size();
        for (i=0;i<n;i++)
        {
            if(component==listComponent.get(i)) return i;
        }
        return -1;
    }

    public void AddNewData(String fileName) throws IOException, ClassNotFoundException {
       // adapter.AddNewData(fileName);
        initGridView();

    }

    private ClickedListener clickedListener = new ClickedListener() {
        @Override
        public void onClick(Component component) {
            curIdx=getIdxByCommponet(component);
            if(curIdx==listComponent.size()-1)
            {
                //add new pic
                Intent intent = new Intent();
                intent.setParam("indexOrder",curIdx);
                Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                intent.setOperation(opt);
                intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                intent.setType("image/*");
                maiAbilitySlice.startAbilityForResult(intent, imgRequestCode);
            }else {

            }

       //     Text textItem = (Text) component.findComponentById(ResourceTable.Id_grid_item_text);
       //     Toast.show(getContext(), textItem.getText());
        }
    };

    private final LongClickedListener longClickListener =
            component -> {

                curIdx=getIdxByCommponet(component);
                //Component
                shadowComponent = null;
                try {
                    if(curIdx<dataList.size()) {
                        shadowComponent = getShadow();
                    }else{
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                shadowComponent.setWidth(component.getWidth());
                shadowComponent.setHeight(component.getHeight());
                shadowComponent.setAlpha(alphaFloat);
                DragFeedbackProvider dragFeedbackProvider =
                        new DragFeedbackProvider(shadowComponent);
                component.startDragAndDrop(new MimeData(), dragFeedbackProvider);
                component.setVisibility(Component.INVISIBLE);
                selectedView = component;
                isViewOnDrag = true;
                if (UP_GRID_TAG.equals(selectedView.getTag())) {
                    if (slice.findComponentById(ResourceTable.Id_grid_view_up) instanceof GridView) {
                        parentView = (GridView) slice.findComponentById(ResourceTable.Id_grid_view_up);
                        //  startAnimation(parentView, animationRotate);
                        isBack = false;
                    }
                }
                bindComponentTransition();
            };
    private void initEventListener() {
        scrollView.setTouchEventListener(
                (component, touchEvent) -> {
                    if (touchEvent.getPointerCount() == 2) {
                       // L.e("GestureDetctorV2: onTouchEvent ： 两个手指");
                        return false;
                    }
                    MmiPoint downScreenPoint = touchEvent.getPointerScreenPosition(touchEvent.getIndex());
                    switch (touchEvent.getAction()) {
                        case TouchEvent.PRIMARY_POINT_DOWN:
                            bMoveTouch=false;
                            currentDragY = (int) downScreenPoint.getY();
                            MmiPoint downPoint = touchEvent.getPointerPosition(touchEvent.getIndex());
                            scrollViewTop = (int) downScreenPoint.getY() - (int) downPoint.getY();
                            scrollViewLeft = (int) downScreenPoint.getX() - (int) downPoint.getX();
                            display.getSize(point);
                            displayHeight = (int) point.getPointY();
                            return true;
                        case TouchEvent.PRIMARY_POINT_UP:
                           // AnimatorProperty animatorProperty = animatorList.get(selectedView);
                           // if (animatorProperty != null && !isBack) {
                                //animatorProperty.setStateChangedListener(null);
                                //animatorProperty.stop();
                                //animatorProperty.rotate(0).setDuration(duration).start();
                                //  startAnimation(selectedView, animationRotate);
                           // }
                            int nowDragY = (int) downScreenPoint.getY();
                            if (isViewOnDrag && bMoveTouch) {
                                selectedView.setScale(1.0f, 1.0f);
                                selectedView.setAlpha(1.0f);
                                selectedView.setVisibility(Component.VISIBLE);
                                //
                                try {
                                    SwapDataByNewOrder();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            isViewOnDrag = false;
                            isScroll = false;
                            bMoveTouch=false;
                            return true;
                        case TouchEvent.HOVER_POINTER_EXIT://CANCEL:

                            break;
                        case TouchEvent.POINT_MOVE:
                            bMoveTouch=true;
                            return printMove(downScreenPoint);
                        default:
                            break;
                    }
                    return false;
                });
    }
    /**
     * 构造方法
     *
     * @param context
     */
    public DragGridView(Context context) {
        this(context, null, "");
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrSet
     */
    public DragGridView(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrSet
     * @param styleName
     */
    public DragGridView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        this.context=context;
    }

    /**
     * 设置数据
     *
     * @param list
     */
    public void setData(List<GridItemInfo> list) throws IOException, ClassNotFoundException {
        this.dataList = list;
        initGridView();

    }

    public void DeleteDataMsg() throws IOException, ClassNotFoundException {
        initGridView();
    }
    /**
     * 设置列数
     *
     * @param column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * method for init view
     */
    public void initGridView() throws IOException, ClassNotFoundException {
        slice = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_dynamicgrid_main, this, true);
       // slice = LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_dynamicgrid_main, this, true);

        if (slice.findComponentById(ResourceTable.Id_grid_layout) instanceof ScrollView) {
            scrollView = (ScrollView) slice.findComponentById(ResourceTable.Id_grid_layout);
        }
        initUpListItem();
        initEventListener();
        //
        int nb=scrollView.getBottom();
       // scrollView.fluentScrollTo(0,nb);
    }

    private void initUpListItem() throws IOException {
        GridView gridView = (GridView) slice.findComponentById(ResourceTable.Id_grid_view_up);
        adapter = new GridAdapter(slice.getContext(), dataList, this.column);
        gridView.setColumnCount(this.column);
        gridView.setAdapter(adapter, longClickListener, clickedListener);
        gridView.setTag(UP_GRID_TAG);
        //
        adapter.setFatherClass(this);
        listComponent=adapter.getComponentList();
    }

    private boolean printMove(MmiPoint downScreenPoint) {
        if (!isViewOnDrag) {
            return true;
        }
        int nidx = this.pointToPosition((int)downScreenPoint.getX(), (int)downScreenPoint.getY());
        if(nidx>=dataList.size()){
            return false;
        }

        int pointX = (int) downScreenPoint.getX();
        int pointY = (int) downScreenPoint.getY();
        int nb=scrollView.getBottom();

        this.exchangeItem(pointX, pointY);
        if (UP_GRID_TAG.equals(selectedView.getTag())) {
            this.swapItems(pointX, pointY);
        }
        int height = displayHeight - (displayHeight - 500);
        if (pointY < height) {
            if (lastPointY < pointY) {
                if (eventHandler != null) {
                    eventHandler.removeAllEvent();
                    lastPointY = 0;
                    return true;
                }
            }
        } else {
            if (lastPointY > pointY) {
                if (eventHandler != null) {
                    eventHandler.removeAllEvent();
                    lastPointY = 0;
                    return true;
                }
            }
        }
        this.handleScroll(pointY);
        lastPointY = pointY;
        return true;
    }

    private void exchangeItem(int pointX, int pointY) {
        if (!isStartupExchage(pointY)) {
            return;
        }
        GridView gridView;
        //
        int curidx=pointToPosition(pointX,pointY);
        if(curidx<0 || curidx>=dataList.size()-1) return;
        parentView.removeComponent(selectedView);
        int addPosition;
        gridView = (GridView) slice.findComponentById(ResourceTable.Id_grid_view_up);
        if (UP_GRID_TAG.equals(selectedView.getTag())) {
            selectedView.setTag(DOWN_GRID_TAG);
            addPosition = 0;
        } else {
            selectedView.setTag(UP_GRID_TAG);
            addPosition = gridView.getChildCount();
        }
        gridView.addComponent(selectedView, addPosition);
        selectedView = gridView.getComponentAt(addPosition);
        parentView = gridView;
        currentDragY = pointY;
    }

    private void swapItems(int pointX, int pointY) {
        if (isViewOnExchange) {
            isViewOnExchange = false;
            return;
        }
        newIdx=-1;
        int currentPosition = parentView.getChildIndex(selectedView);
        int endPosition = this.pointToPosition(pointX, pointY);
        if(endPosition<0) return;
        if (endPosition == INVALID_POSITION || endPosition == currentPosition) {
            return;
        }
        parentView.removeComponent(selectedView);
        parentView.addComponent(selectedView, endPosition);
        currentDragY = pointY;
        selectedView = parentView.getComponentAt(endPosition);
        //exchange id order
        newIdx=endPosition;
    }
    private void SwapDataByNewOrder() throws IOException, ClassNotFoundException {
        boolean bSuc;
        bSuc=true;
        Component component;
        for (int i=0;i<dataList.size();i++) {
            component=parentView.getComponentAt(i);
            String tag= (String) component.getTag();
            if(tag=="addGrid") {
                    try {
                        shadowComponent.setVisibility(INVISIBLE);
                        initGridView();
                        parentView.invalidate();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;

            }
            int idx=getIdxByCommponet(component);
            if(idx<0) {
                return;
            }
            if(idx>=dataList.size()) {
                return;
            }
            GridItemInfo data=dataList.get(idx);
            if (data==null)
            {
                return;
            }
            dataList.get(idx).setIdOrder(i);
        }

//        int curOrder=dataList.get(curIdx).getIdOrder();
//        int newOrder=dataList.get(newIdx).getIdOrder();
//        dataList.get(curIdx).setIdOrder(newOrder);
//        dataList.get(newIdx).setIdOrder(curOrder);
        //
        newIdx=curIdx=-1;
    }

    private int pointToPosition(int pointX, int pointY) {
        int currentOffset = 100;
        int currentX = pointX - scrollViewLeft - currentOffset;
        int currentY = pointY + scrollView.getScrollValue(Component.VERTICAL) - scrollViewTop - currentOffset;
        int childCount = parentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Rect child = parentView.getComponentAt(i).getComponentPosition();
            if (child.isInclude(currentX, currentY)) {
                return i;
            }
        }
        return INVALID_POSITION;
    }

    private void handleScroll(int pointY) {
        int scrollUp = -300;
        int scrollDown = 300;
        if (pointY < displayHeight - (displayHeight - 450)) {
            isScroll = true;
            scroll(scrollUp);
        } if (pointY > displayHeight - 300) {
            isScroll = true;
            scroll(scrollDown);
        }
    }


    private void scroll(int dy) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                eventHandler.removeAllEvent();
                if (isScroll) {
                    scroll(dy);
                    scrollView.fluentScrollByY(dy);
                }
            }
        };
        eventHandler.postTask(runnable, 200);
    }


    private void bindComponentTransition() {
        if (parentView != null && parentView.getComponentTransition() == null) {
            ComponentTransition transition = new ComponentTransition();
            transition.removeTransitionType(ComponentTransition.SELF_GONE);
            transition.removeTransitionType(ComponentTransition.OTHERS_GONE);
            transition.removeTransitionType(ComponentTransition.CHANGING);
            parentView.setComponentTransition(transition);
        }
    }

    private Component getShadow() throws IOException {
        Component itemLayout =
            LayoutScatter.getInstance(slice.getContext()).parse(ResourceTable.Layout_grid_item, null, false);
        if (itemLayout.findComponentById(ResourceTable.Id_grid_item_image) instanceof Image) {
            Image imageItem = (Image) itemLayout.findComponentById(ResourceTable.Id_grid_item_image);
            //read file data and clone
            //int idx=getIdxByCommponet(itemLayout);
            GridItemInfo data=dataList.get(curIdx);
            OperToolImagFile fileOper=new OperToolImagFile();
            //String fileName=new File(context.getFilesDir(),"icon_test.png").getPath();
            PixelMap img=fileOper.ReadPixelMapFile(data.getFileName());
            imageItem.setPixelMap(img);
            imageItem.setWidth(743);
            imageItem.setHeight(464);
            //imageItem.setPixelMap(ResourceTable.Media_icon);
            imageItem.setScale(1.1f, 1.1f);
            itemLayout.setScale(1.1f, 1.1f);
        }
        return itemLayout;
    }

    private boolean isStartupExchage(int pointY) {
        int scrollY = isScroll ? scrollView.getScrollValue(Component.VERTICAL) : 0;
        int offsetY = pointY - currentDragY;
        offsetY = offsetY < 0 ? offsetY - scrollY : offsetY + scrollY;
        Rect currentRect = selectedView.getComponentPosition();
        int curOffsetY = currentRect.getCenterY() + offsetY;
        if (UP_GRID_TAG.equals(selectedView.getTag())) {
            isViewOnExchange = curOffsetY > parentView.getComponentPosition().bottom;
        }else if (ADD_GRID_TAG.equals(selectedView.getTag())){
            isViewOnExchange=false;
        }
        return isViewOnExchange;
    }

    /**
     * 取消动画
     */
    public void resetAnimation() {
//        animatorList.forEach((component, animatorProperty) -> {
//            animatorProperty.setStateChangedListener(null);
//            animatorProperty.stop();
//            animatorProperty.rotate(0).setDuration(duration).start();
//        });
    }

    /**
     * 开始动画
     *
     * @param gridView
     * @param rotate
     */
    private void startAnimation(ComponentContainer gridView, int rotate) {
//        resetAnimation();
//        animatorList.clear();
//        int childCount = gridView.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            AnimatorProperty animatorProperty = new AnimatorProperty();
//            animatorProperty.setTarget(gridView.getComponentAt(i));
//            if (i % 2 == 0) {
//                animatorProperty.rotate(rotate).setDuration(duration);
//            } else {
//                animatorProperty.rotate(-rotate).setDuration(duration);
//            }
//            animatorProperty.setStateChangedListener(new Animator.StateChangedListener() {
//                @Override
//                public void onStart(Animator animator) {
//
//                }
//
//                @Override
//                public void onStop(Animator animator) {
//
//                }
//
//                @Override
//                public void onCancel(Animator animator) {
//
//                }
//
//                @Override
//                public void onEnd(Animator animator) {
//                    float rotation = animatorProperty.getTarget().getRotation();
//                    animatorProperty.rotate(-rotation).setDuration(duration);
//                    animatorProperty.start();
//                }
//
//                @Override
//                public void onPause(Animator animator) {
//
//                }
//
//                @Override
//                public void onResume(Animator animator) {
//
//                }
//            });
//            animatorProperty.start();
//            animatorList.put(gridView.getComponentAt(i), animatorProperty);
//        }
    }

    /**
     * 单独启动其中一个的动画
     *
     * @param component
     * @param rotate
     */
    private void startAnimation(Component component, int rotate) {
        return;
//        AnimatorProperty animatorProperty = new AnimatorProperty();
//        animatorProperty.setTarget(component);
//        animatorProperty.rotate(-rotate).setDuration(duration);
//        animatorProperty.setStateChangedListener(new Animator.StateChangedListener() {
//            @Override
//            public void onStart(Animator animator) {
//            }
//
//            @Override
//            public void onStop(Animator animator) {
//            }
//
//            @Override
//            public void onCancel(Animator animator) {
//            }
//
//            @Override
//            public void onEnd(Animator animator) {
//                float rotation = animatorProperty.getTarget().getRotation();
//                animatorProperty.rotate(-rotation).setDuration(duration);
//                animatorProperty.start();
//            }
//
//            @Override
//            public void onPause(Animator animator) {
//            }
//
//            @Override
//            public void onResume(Animator animator) {
//            }
//        });
//        animatorProperty.start();
//        animatorList.put(component, animatorProperty);
    }


    /**
     * 返回标识
     *
     * @return 返回标识
     */
    public boolean isBack() {
        return isBack;
    }

    /**
     * 设置返回标识
     *
     * @param back
     */
    public void setBack(boolean back) {
        isBack = back;
    }

}
