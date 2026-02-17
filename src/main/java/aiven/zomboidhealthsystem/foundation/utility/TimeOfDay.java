package aiven.zomboidhealthsystem.foundation.utility;

public class TimeOfDay {
    private long time;

    public TimeOfDay(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addTime(long time) {
        this.setTime(getTime() + time);
    }

    public String getModTime() {
        int seconds = (int) (getTime() * 3.6F);
        int minutes = seconds / 60 % 60;
        int hours = (seconds / 3600 + 8) % 24;
        return hours + (minutes >= 10 ? ":" : ":0") + minutes;
    }
}
