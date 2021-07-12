package com.example.practical.module.media_list;

import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.practical.R;
import com.example.practical.api.ApiClient;
import com.example.practical.api.WebserviceBuilder;
import com.example.practical.api.modal.MediaModal;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MediaListViewModel extends ViewModel {
    ObservableField<MediaModal> mediaList = new ObservableField<MediaModal>();
    PublishSubject<MediaModal> test;

    void getMediaList(ProgressDialog pd) {
        Observable<MediaModal> listObject = ApiClient.getClient().create(WebserviceBuilder.class).getMediaList();
        Single.fromObservable(listObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<MediaModal>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NotNull MediaModal mediaModalResponse) {
                        mediaList.set(mediaModalResponse);
                        test.onNext(mediaModalResponse);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        e.printStackTrace();
                        pd.hide();
                        Toast.makeText(pd.getContext(), pd.getContext().getString(R.string.general_error), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
