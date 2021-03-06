package javinator9889.bitcoinpools;

import java.util.concurrent.TimeUnit;

/**
 * Created by Javinator9889 on 23/01/2018.
 * Contains constant values that will be used
 */

public class Constants {
    public static final boolean PERSISTED = true;
    public static final long SCHEDULING_TIME = TimeUnit.HOURS.toMillis(1);
    public static final long BACKOFF_CRITERIA = TimeUnit.SECONDS.toMillis(30);
    public static final int JOB_ID = 1;
    public static final String JOBINFO = "(job:2/javinator9889.bitcoinpools/.BackgroundJobs.CacheJobSchedulerService)";
    public static final long MILLIS_A_DAY = 86400000;

    public static class PAYMENTS {
        public static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgt71diQE3dVAJ/KEJSpt+ZIEeeKDOWl5cBdwRirkjiVPtzwbOlOyJsf+tJzQrYvxJejmkfdwR5TlG4Z+NAZDtcS4mq63JVoPyEbmx0wvVYC3+zav2MbJO9P/gSmwTK0KGwVSyItcH5sqXjK9Mv280uj2jM0IMW0UpM91vzeitCGCbJRwMe1CnzLzFPFI01YJ/QjG+1KY7MzIhn3P2ZbS9C7fhP0BwJIBPoZJkp64pKhXf7iI5qsbZGby4V+iQiU5ONiS+ggy8X076IAB1DijL90BUnbTXCwa1WufChb3da7xV/AiPEHl9UJ2J70I3+/1Dx9MXOrYkBmOKAYFLJlcQwIDAQAB";
        public static final String[] GOOGLE_CATALOG = new String[]{"in_app_purchases_donations",
                "in_app_purchases_donations_2", "in_app_purchases_donations_3",
                "in_app_purchases_donations_5", "in_app_purchases_donations_8",
                "in_app_purchases_donations_13"};
        public static final String PAYPALME = "https://paypal.me/Javinator9889";
    }
    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=javinator9889.bitcoinpools";
    public static final String API_URL = "https://api.coindesk.com/v1/bpi/historical/close.json";
    public static final class SHARED_PREFERENCES {
        public static final String SHARED_PREFERENCES_KEY = "javinator9889.bitcoinpools.usrPreferences";
        public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
        public static final String NOTIFIED_HIGH = "notified_high";
        public static final String NOTIFIED_LOW = "notified_low";
        public static final String DAYS_TO_CHECK = "days_to_check";
        public static final String VALUE_TO_CHECK = "value_to_check";
        public static final String SHARED_PREFERENCES_INITIALIZED = "initialized";
        public static final String APP_VERSION = "APP_VERSION";
        public static final String CACHE_JOB = "CACHE_JOB";
    }
    public static final String CHANNEL_ID = "javinator9889.bitcoinpools.Alerts";
    public static final int NOTIFICATION_ID = 1;
    public static final int REQUEST_CODE = 0;
    public static final String GITHUB_USER = "Javinator9889";
    public static final String GITHUB_REPO = "BitCoinPools";
    public static final String STATS_URL = "https://api.blockchain.info/stats";
    public static final String MARKET_NAME = "market_price_usd";
    public static final String POOLS_URL = "https://api.blockchain.info/pools?timespan=";

    public static final class LOG {
        public static final String UNCAUGHT_ERROR = "Uncaught error on: ";

        public static final String BCTAG = "BitCoinApp";
        public static final String NO_INIT = "Unable to init current activity: ";
        public static final String INIT_PREF = "Initialising user shared preferences";
        public static final String RESTART_JOB = "Restarting background jobs...";
        public static final String CREATED_APP = "Correctly created application";

        public static final String LTAG = "License";
        public static final String INIT_L = "Created license page";

        public static final String MATAG = "MainActivity";
        public static final String CREATING_MAINVIEW = "Creating application Main View";
        public static final String CREATING_CHART = "Creating application chart";
        public static final String INIT_VALUES = "Initialising application values";
        public static final String LISTENING = "Listening to buttons";
        public static final String LOADING_MPU = "Loading MPU in a new thread...";
        public static final String LOADING_RD = "Loading data in a new thread...";
        public static final String LOADING_CHART = "Loading PieChart in a new thread...";
        public static final String LOADING_TABLE = "Loading table in a new thread...";
        public static final String MARKET_PRICE_ERROR = "Error on MainActivity.initMPU(): ";
        public static final String DATA_ERROR = "Error on MainActivity.initRD(): ";
        public static final String JOIN_ERROR = "Failed to join thread ";

        public static final String STAG = "SpinnerActivity";
        public static final String INIT_SETTINGS_VIEW = "Starting settings view...";
        public static final String INIT_SPINNER = "Starting configurations of spinners based on user preferences";
        public static final String INIT_SWITCH = "Configuring options for switch in settings activity. Current state: ";
        public static final String CHANGE_PREFERENCES = "Changing preferences on ";
        public static final String BACK_TO_MC = "Going back to MainActivity. Saving data...";

        public static final String JTAG = ".JobSchedulerService";
        public static final String RECEIVED_JOB = "Correctly received job";
        public static final String STARTING_JOB = "Starting current job and notification handler. Job ID: ";
        public static final String STOPPING_JOB = "Stopping current job. The job was interrupted. Job ID: ";

        public static final String NTAG = ".NotificationHandler";
        public static final String CREATING_NOTIFICATION = "Creating current notification";
        public static final String CURRRENT_NOT_SETTINGS = "Current notification settings: (ENABLED, NOTIFIED_HIGH_ NOTIFIED_LOW, SPECIFIC_VALUE, MPU)";
        public static final String NOTIFYING = "Notifying to user";
        public static final String NNOTIFYING = "Not notifying to user";

        public static final String CTAG = ".CheckUpdates";
        public static final String NO_INFO = "The API was unable to get the package information. Full trace: ";
        public static final String NEW_VERSION = "There is a new version available. Versions: (current | new) ";
        public static final String DOW_NOTIFICATION = "Creating a notification with download button";
        public static final String NDOW_NOTIFICATION = "Creating a notification without download button";

        public static final String NCTAG = ".NetworkConnection";
        public static final String CONNECTION = "Connecting to GitHub and getting latest information";
        public static final String JSONERROR = "Error while trying to read JSON from URL. Full trace: ";
    }
}
