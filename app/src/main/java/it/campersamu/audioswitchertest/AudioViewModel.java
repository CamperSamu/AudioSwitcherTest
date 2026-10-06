package it.campersamu.audioswitchertest;

import android.app.Application;
import android.media.MediaRoute2Info;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AudioViewModel extends AndroidViewModel {
    private final AudioRepository repository;

    public AudioViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRepository(application);
        refreshRoutes();
    }

    public LiveData<List<MediaRoute2Info>> getAvailableRoutes() {
        return repository.getAvailableRoutes();
    }

    public void refreshRoutes() {
        repository.refreshRoutes();
    }

    public void selectRoute(MediaRoute2Info route) {
        repository.selectRoute(route);
    }
}
