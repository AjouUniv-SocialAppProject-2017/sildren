package media.socialapp.sildren.utilities;

import java.util.List;

import media.socialapp.sildren.DataModels.Route;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
