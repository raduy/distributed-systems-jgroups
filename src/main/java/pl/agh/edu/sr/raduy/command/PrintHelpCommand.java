package pl.agh.edu.sr.raduy.command;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class PrintHelpCommand implements ICommand {

    @Override
    public void execute() {
        System.out.println("Help");
    }
}
