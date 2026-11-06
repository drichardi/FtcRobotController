# Robot designs

This folder holds robot designs for the [Robot Simulator](../curriculum/simulator.md). Each
`.robot.json` file lists a robot's parts: where each one is, what it's attached to, and which
Control Hub port its cable plugs into. The simulator works out how the robot drives from its
parts: bigger wheels go faster, a TETRIX-style motor is slower and has 1440 ticks per turn, and so on.

- **`CourseBot.robot.json`** is the course robot that every lesson uses. Leave it as it is.
- Your group's designs go here too, so everyone gets them when you **pull** in GitHub Desktop.

Pick a design in the simulator's **Robot** panel. **Parts list** shows every part it uses.

**Build...** opens the robot builder: pick a part from the menu, point at the robot, and click to
place it. Parts snap where they belong, and each motor, servo, and sensor gets a port. **Save and
drive it** saves the design here and puts it in the simulator. You can start from CourseBot, but
save your version with your own name: CourseBot itself stays as it is.

Nothing in this folder goes onto the real robot.
