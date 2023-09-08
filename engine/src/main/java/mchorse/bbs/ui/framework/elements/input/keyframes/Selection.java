package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.utils.keyframes.Keyframe;

public enum Selection
{
    NOT_SELECTED
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }
    },
    KEYFRAME
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.getTick();
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.getValue();
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            keyframe.setTick((long) x);
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            keyframe.setValue(y);
        }
    },
    LEFT_HANDLE
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.getLx();
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.getLy();
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            keyframe.setLx((float) x);

            if (opposite)
            {
                keyframe.setRx(keyframe.getLx());
            }
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            keyframe.setLy((float) y);

            if (opposite)
            {
                keyframe.setRy(-keyframe.getLy());
            }
        }
    },
    RIGHT_HANDLE
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.getRx();
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.getRy();
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            keyframe.setRx((float) x);

            if (opposite)
            {
                keyframe.setLx(keyframe.getRx());
            }
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            keyframe.setRy((float) y);

            if (opposite)
            {
                keyframe.setLy(-keyframe.getRy());
            }
        }
    };

    public abstract double getX(Keyframe keyframe);

    public abstract double getY(Keyframe keyframe);

    public abstract void setX(Keyframe keyframe, double x, boolean opposite);

    public abstract void setY(Keyframe keyframe, double y, boolean opposite);
}