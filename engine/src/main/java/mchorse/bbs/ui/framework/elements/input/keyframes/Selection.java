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
            return keyframe.tick;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.value;
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
            return keyframe.lx;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.ly;
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            keyframe.lx = (float) x;

            if (opposite)
            {
                keyframe.rx = keyframe.lx;
            }
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            keyframe.ly = (float) y;

            if (opposite)
            {
                keyframe.ry = -keyframe.ly;
            }
        }
    },
    RIGHT_HANDLE
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.rx;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.ry;
        }

        @Override
        public void setX(Keyframe keyframe, double x, boolean opposite)
        {
            keyframe.rx = (float) x;

            if (opposite)
            {
                keyframe.lx = keyframe.rx;
            }
        }

        @Override
        public void setY(Keyframe keyframe, double y, boolean opposite)
        {
            keyframe.ry = (float) y;

            if (opposite)
            {
                keyframe.ly = -keyframe.ry;
            }
        }
    };

    public abstract double getX(Keyframe keyframe);

    public abstract double getY(Keyframe keyframe);

    public abstract void setX(Keyframe keyframe, double x, boolean opposite);

    public abstract void setY(Keyframe keyframe, double y, boolean opposite);
}