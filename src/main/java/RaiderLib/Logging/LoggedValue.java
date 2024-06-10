package RaiderLib.Logging;

public class LoggedValue {
    public final LoggableType type;
    private final Object value;

    public boolean sendToNT = true;

    enum LoggableType {
        Raw,
        Boolean,
        Integer,
        Float,
        Double,
        String,
        BooleanArray,
        IntegerArray,
        FloatArray,
        DoubleArray,
        StringArray
    }

    LoggedValue(byte[] value) {
        type = LoggableType.Raw;
        this.value = value;
    }

    LoggedValue(boolean value) {
        type = LoggableType.Boolean;
        this.value = value;
    }

    LoggedValue(long value) {
        type = LoggableType.Integer;
        this.value = value;
    }

    LoggedValue(float value) {
        type = LoggableType.Float;
        this.value = value;
    }

    LoggedValue(double value) {
        type = LoggableType.Double;
        this.value = value;
    }

    LoggedValue(String value) {
        type = LoggableType.String;
        if (value != null) {
            this.value = value;
        } else {
            this.value = "";
        }
    }

    LoggedValue(boolean[] value) {
        type = LoggableType.BooleanArray;
        this.value = value;
    }

    LoggedValue(long[] value) {
        type = LoggableType.IntegerArray;
        this.value = value;
    }

    LoggedValue(float[] value) {
        type = LoggableType.FloatArray;
        this.value = value;
    }

    LoggedValue(double[] value) {
        type = LoggableType.DoubleArray;
        this.value = value;
    }

    LoggedValue(String[] value) {
        type = LoggableType.StringArray;
        this.value = value;
    }
}
