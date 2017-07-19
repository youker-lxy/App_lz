package gson;

import java.util.List;

/**
 * Created by youker on 2017/7/8 0008.
 */

public class DataBean {

    /**
     * errno : 0
     * data : {"count":2,"datastreams":[{"datapoints":[{"at":"2017-07-02-17:19:29.780","value":41}],"id":"Air_Humt"},{"datapoints":[{"at":"2017-07-02-17:19:29.780","value":28}],"id":"Air_Temp"}]}
     * error : succ
     */

    private int errno;
    private Data data;
    private String error;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class Data {
        /**
         * count : 2
         * datastreams : [{"datapoints":[{"at":"2017-07-02-17:19:29.780","value":41}],"id":"Air_Humt"},{"datapoints":[{"at":"2017-07-02-17:19:29.780","value":28}],"id":"Air_Temp"}]
         */

        private int count;
        private List<Datastreams> datastreams;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Datastreams> getDatastreams() {
            return datastreams;
        }

        public void setDatastreams(List<Datastreams> datastreams) {
            this.datastreams = datastreams;
        }

        public static class Datastreams {
            /**
             * datapoints : [{"at":"2017-07-02-17:19:29.780","value":41}]
             * id : Air_Humt
             */

            private String id;
            private List<Datapoints> datapoints;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public List<Datapoints> getDatapoints() {
                return datapoints;
            }

            public void setDatapoints(List<Datapoints> datapoints) {
                this.datapoints = datapoints;
            }

            public static class Datapoints {
                /**
                 * at : 2017-07-02-17:19:29.780
                 * value : 41
                 */

                private String at;
                private int value;

                public String getAt() {
                    return at;
                }

                public void setAt(String at) {
                    this.at = at;
                }

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }
        }
    }
}
