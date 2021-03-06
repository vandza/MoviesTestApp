package com.covain.movies.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.covain.movies.Constants;
import com.covain.movies.MoviesAdapter;
import com.covain.movies.model.MoviesResponse;
import com.covain.movies.movies.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.POST;

public class HomeFragment extends BaseFragment {

    @Bind(R.id.movies_list_view)
    RecyclerView mMoviesListView;

    private Retrofit mRetrofit;
    private NetworkService mNetworkService;
    private MoviesAdapter mMoviesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        mMoviesAdapter = new MoviesAdapter(getContext());

        mMoviesListView.setHasFixedSize(true);
        mMoviesListView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mMoviesListView.setAdapter(mMoviesAdapter);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mNetworkService = mRetrofit.create(NetworkService.class);
        loadMovies();
        return view;
    }

    private void loadMovies() {
        mMainActivity.showLoading();
        Call<MoviesResponse> moviesCall = mNetworkService.getMovies();
        moviesCall.enqueue(mNetworkCallback);
    }

    private Callback<MoviesResponse> mNetworkCallback = new Callback<MoviesResponse>() {
        @Override
        public void onResponse(Response<MoviesResponse> response, Retrofit retrofit) {
            mMoviesAdapter.setData(response.body().getMovies());
            mMainActivity.hideLoading();
        }

        @Override
        public void onFailure(Throwable t) {
            mMainActivity.hideLoading();
            Toast.makeText(mMainActivity, "Failed to load movies", Toast.LENGTH_LONG).show();
        }
    };

    public interface NetworkService {
        @POST("top_rated?api_key=" + Constants.API_KEY)
        Call<MoviesResponse> getMovies();
    }
}
