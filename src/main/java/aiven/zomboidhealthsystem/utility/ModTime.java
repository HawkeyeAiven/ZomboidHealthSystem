package aiven.zomboidhealthsystem.utility;

public class ModTime {
    protected long time;

    public ModTime(long time) {
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

    public int getSeconds() {
        return (int) (time * 3.6F);
    }

    public int getSecond() {
        return getSeconds() % 60;
    }

    public int getMinutes() {
        return getSeconds() / 60;
    }

    public int getMinute() {
        return getMinutes() % 60;
    }

    public int getHours() {
        return getSeconds() / 3600 + 8;
    }

    public int getHour() {
        return getHours() % 24;
    }

    @Override
    public String toString() {
        return getHour() + (getMinute() >= 10 ? ":" : ":0") + getMinute();
    }
}
