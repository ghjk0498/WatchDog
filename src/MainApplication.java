import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainApplication {

	public static void main(String[] args) {
		while (true) {
			try {
				ProcessHandle processHandle = findProcess("notepad.exe");
				if (processHandle == null) {
					System.out.println("Execute");
					runProcess(new String[] { "C:\\Windows\\System32\\notepad.exe" });
//					runProcess(args);
				} else {
					// 테스트 용
//					killProcess(processHandle);
				}

				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void killProcess(ProcessHandle processHandle) {
		Long parentProcessId = processHandle.parent().map(ProcessHandle::pid).orElse(-1L);
		if (parentProcessId.equals(-1L)) {
			processHandle.destroy();
			return;
		}

		ProcessHandle parentProcess = ProcessHandle.of(parentProcessId).get();
		String parentProcessCommand = parentProcess.info().command().get();
		String processCommand = processHandle.info().command().get();

//		System.out.println(processCommand);
//		System.out.println(parentProcessCommand);
		if (processCommand.equals(parentProcessCommand)) {
			parentProcess.destroy();
		} else {
			processHandle.destroy();
		}
	}

	private static void runProcess(String[] command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ProcessHandle findProcess(String name) {
		ProcessHandle result = null;
		for (ProcessHandle processHandle : ProcessHandle.allProcesses().collect(Collectors.toList())) {
			String path = processHandle.info().command().orElse("");
			if (name.toLowerCase().equals(new File(path).getName().toLowerCase())) {
				result = processHandle;
			}
		}
		return result;
	}

	private static String processDetails(ProcessHandle process) {
		return String.format("%8d %8s %10s %26s %-40s",
	            process.pid(),
	            text(process.parent().map(ProcessHandle::pid)),
	            text(process.info().user()),
	            text(process.info().startInstant()),
	            text(process.info().command())
			);
	}

	private static String text(Optional<?> optional) {
	    return optional.map(Object::toString).orElse("-");
	}

}