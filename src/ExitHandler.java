public class ExitHandler implements Runnable{
    @Override
    public void run() {
        System.out.println("finito");
        Server.stop();
        System.exit(0);
    }
}
