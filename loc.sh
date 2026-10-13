#!/bin/bash
normal_java_files=$(find . -type f -name "*.java" ! -name "*Test.java" | wc -l)
normal_java_lines=$(find . -type f -name "*.java" ! -name "*Test.java" -exec cat {} + 2>/dev/null | wc -l)

test_java_files=$(find . -type f -name "*Test.java" | wc -l)
test_java_lines=$(find . -type f -name "*Test.java" -exec cat {} + 2>/dev/null | wc -l)

gd_files=$(find . -type f -name "*.gd" | wc -l)
gd_lines=$(find . -type f -name "*.gd" -exec cat {} + 2>/dev/null | wc -l)

printf "Java files      : %d\nJava LOC        : %d\n\n" "$normal_java_files" "$normal_java_lines"
printf "Java Test files : %d\nJava Test LOC   : %d\n\n" "$test_java_files" "$test_java_lines"
printf "Godot files     : %d\nGodot LOC       : %d\n" "$gd_files" "$gd_lines"