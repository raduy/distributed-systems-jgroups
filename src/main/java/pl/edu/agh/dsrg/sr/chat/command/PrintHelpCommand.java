package pl.edu.agh.dsrg.sr.chat.command;

/**
 * @author Lukasz Raduj <raduj.lukasz@gmail.com>
 */
public class PrintHelpCommand implements ICommand {

    @Override
    public void execute() {
        System.out.println("Help");
    }
}
