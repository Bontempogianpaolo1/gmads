package gmads.it.gmads_lab1.Map.main.m;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import gmads.it.gmads_lab1.Book;
import gmads.it.gmads_lab1.Map.common.maps.MapBitmapCache;
import gmads.it.gmads_lab1.Map.common.maps.PulseOverlayLayout;
import gmads.it.gmads_lab1.Map.common.mvp.MvpFragment;
import gmads.it.gmads_lab1.Map.common.transitions.ScaleDownImageTransition;
import gmads.it.gmads_lab1.Map.common.transitions.TransitionUtils;
import gmads.it.gmads_lab1.Map.common.views.HorizontalRecyclerViewScrollListener;
import gmads.it.gmads_lab1.Map.common.views.TranslateItemAnimator;
import gmads.it.gmads_lab1.Map.main.MainActivity;
import gmads.it.gmads_lab1.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends MvpFragment<DetailsFragmentView, DetailsFragmentPresenter>
        implements DetailsFragmentView, OnMapReadyCallback, SearchBooksAdapter.OnPlaceClickListener, HorizontalRecyclerViewScrollListener.OnItemCoverListener {
    public static final String TAG = DetailsFragment.class.getSimpleName();

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.container) FrameLayout containerLayout;
    @BindView(R.id.mapOverlayLayout)
    PulseOverlayLayout mapOverlayLayout;
    private List<Book> baliPlaces;
    private SearchBooksAdapter baliAdapter;
    private String currentTransitionName;
    private Scene detailsScene;

    public static Fragment newInstance(final Context ctx) {
        DetailsFragment fragment = new DetailsFragment();
        ScaleDownImageTransition transition = new ScaleDownImageTransition(ctx, MapBitmapCache.instance().getBitmap());
        transition.addTarget(ctx.getString(R.string.mapPlaceholderTransition));
        transition.setDuration(600);
        fragment.setEnterTransition(transition);
        return fragment;
    }

    @Override
    protected DetailsFragmentPresenter createPresenter() {
        return new DetailsFragmentPresenterImpl();
    }

    @Override
    public void onBackPressed() {
        if (detailsScene != null) {
            presenter.onBackPressedWithScene();
        } else {
            ((MainActivity) getActivity()).superOnBackPressed();
        }
    }

    private View getSharedViewByPosition(final int childPosition) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            if (childPosition == recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i))) {
                return recyclerView.getChildAt(i);
            }
        }
        return null;
    }

    @Override
    public int getLayout() {
        return R.layout.layout_map_recycle;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBaliData();
        setupMapFragment();
        setupRecyclerView();
    }

    private void setupBaliData() {
        presenter.provideBaliData();
    }

    private void setupMapFragment() {
        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
    }

    private void setupRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        baliAdapter = new SearchBooksAdapter(this, getActivity());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapOverlayLayout.setupMap(googleMap);
        setupGoogleMap();
        addDataToRecyclerView();
    }

    private void setupGoogleMap() {
        presenter.moveMapAndAddMarker();
    }

    private void addDataToRecyclerView() {
        recyclerView.setItemAnimator(new TranslateItemAnimator());
        recyclerView.setAdapter(baliAdapter);
        baliAdapter.setBooksList(baliPlaces);
        recyclerView.addOnScrollListener(new HorizontalRecyclerViewScrollListener(this));
    }

    @Override
    public void onPlaceClicked(final View sharedView, final String transitionName, final int position) {
        currentTransitionName = transitionName;
        detailsScene = DetailsLayout.showScene(getActivity(), containerLayout, sharedView, transitionName, baliPlaces.get(position));
        drawRoute(position);
        hideAllMarkers();
    }

    private void drawRoute(final int position) {
        presenter.drawRoute(mapOverlayLayout.getCurrentLatLng(), position);
    }

    private void hideAllMarkers() {
        mapOverlayLayout.setOnCameraIdleListener(null);
        mapOverlayLayout.hideAllMarkers();
    }

    @Override
    public void drawPolylinesOnMap(final ArrayList<LatLng> polylines) {
        getActivity().runOnUiThread(() -> mapOverlayLayout.addPolyline(polylines));
    }

    @Override
    public void provideBaliData(final List<Book> places) {
        baliPlaces = places;
    }

    @Override
    public void onBackPressedWithScene(final LatLngBounds latLngBounds) {
        int childPosition = TransitionUtils.getItemPositionFromTransition(currentTransitionName);
        DetailsLayout.hideScene(getActivity(), containerLayout, getSharedViewByPosition(childPosition), currentTransitionName);
        notifyLayoutAfterBackPress(childPosition);
        mapOverlayLayout.onBackPressed(latLngBounds);
        detailsScene = null;
    }

    private void notifyLayoutAfterBackPress(final int childPosition) {
        containerLayout.removeAllViews();
        containerLayout.addView(recyclerView);
        recyclerView.requestLayout();
        baliAdapter.notifyItemChanged(childPosition);
    }

    @Override
    public void moveMapAndAddMaker(final LatLngBounds latLngBounds) {
        mapOverlayLayout.moveCamera(latLngBounds);
        mapOverlayLayout.setOnCameraIdleListener(() -> {
            for (int i = 0; i < baliPlaces.size(); i++) {
                mapOverlayLayout.createAndShowMarker(i, new LatLng(baliPlaces.get(i).get_geoloc().getLat(),baliPlaces.get(i).get_geoloc().getLng()));
            }
            mapOverlayLayout.setOnCameraIdleListener(null);
        });
        mapOverlayLayout.setOnCameraMoveListener(mapOverlayLayout::refresh);
    }

    @Override
    public void updateMapZoomAndRegion(final LatLng northeastLatLng, final LatLng southwestLatLng) {
        getActivity().runOnUiThread(() -> {
            mapOverlayLayout.animateCamera(new LatLngBounds(southwestLatLng, northeastLatLng));
            mapOverlayLayout.setOnCameraIdleListener(() -> mapOverlayLayout.drawStartAndFinishMarker());
        });
    }

    @Override
    public void onItemCover(final int position) {
        mapOverlayLayout.showMarker(position);
    }
}