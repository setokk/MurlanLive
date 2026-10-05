#!/bin/bash

INSTANCE="0"
GODOT="/c/Users/setokk/Downloads/Godot_v4.7.2-stable_win64.exe/Godot_v4.7.2-stable_win64.exe"
PROJECT="../../MuLiveUI"

while [[ "$#" -gt 0 ]]; do
    case "$1" in
        --godot|-g)
            if [[ -z "${2:-}" ]]; then
                echo "Error: missing path after $1"
                exit 1
            fi
            GODOT="$2"
            shift 2
            ;;
        0|1|2|3)
            INSTANCE="$1"
            shift
            ;;
        *)
            echo "Usage: $0 {0|1|2|3} [-g|--godot PATH]"
            exit 1
            ;;
    esac
done

if [[ ! -f "$GODOT" ]]; then
    echo "Error: Godot executable not found: $GODOT"
    exit 1
fi

if [[ ! -f "$PROJECT/project.godot" ]]; then
    echo "Error: project.godot not found in: $PROJECT"
    exit 1
fi

echo "Starting instance $INSTANCE..."
echo "Godot: $GODOT"
echo "Project: $PROJECT"

"$GODOT" --path "$PROJECT" -- --test-instance="$INSTANCE" &
