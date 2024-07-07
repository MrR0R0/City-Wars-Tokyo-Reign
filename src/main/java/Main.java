import app.ProgramController;
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
        ProgramController controller = new ProgramController();
        controller.run();
    }
}