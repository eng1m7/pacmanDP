public class SpeedSystem {
    
    public interface SpeedComponent {
        int getSpeed();
    }

    public static class BasicSpeed implements SpeedComponent {

        @Override
        public int getSpeed() {
            return 8;
        }
    }

    public static class SpeedDecorator implements SpeedComponent {

        protected SpeedComponent speedComponent;

        public SpeedDecorator(SpeedComponent speedComponent) {
            this.speedComponent = speedComponent;
        }

        @Override
        public int getSpeed() {
            return speedComponent.getSpeed();
        }
    }

    public static class FastPacMan extends SpeedDecorator {

        public FastPacMan(SpeedComponent speedComponent) {
            super(speedComponent);
        }

        @Override
        public int getSpeed() {
            return speedComponent.getSpeed() + 9;
        }
    }
    
}
