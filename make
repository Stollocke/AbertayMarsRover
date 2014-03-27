clear
echo "NXT Java build program"
echo
echo "Compiling..."
nxjc Main.java > /dev/null
echo "Compilation successful"
echo "Linking..."
nxjlink -v -o Main.nxj Main > /dev/null
echo "Linking successful"
echo "Uploading program to NXT brick…"
nxjupload Main.nxj > /dev/null
echo "Upload successfull."
echo
echo "You can now disconnect the NXT brick."