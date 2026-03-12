# Show-FolderTree.ps1
# Displays a tree-like structure of all files and folders in a given directory
# run: .\Show-FolderTree.ps1 -Path "C:\Users\lhacenmed\AndroidStudioProjects\Budget\app\src"
param (
    [Parameter(Mandatory = $false)]
    [string]$Path = ".",

    [Parameter(Mandatory = $false)]
    [switch]$ShowHidden
)

# Define box-drawing characters via Unicode code points
# so the script file stays pure ASCII and encoding never matters
$BRANCH_MID  = [char]0x251C + [char]0x2500 + [char]0x2500 + " "  # ├──
$BRANCH_LAST = [char]0x2514 + [char]0x2500 + [char]0x2500 + " "  # └──
$PIPE        = [char]0x2502 + "   "                               # │
$BLANK       = "    "

function Show-Tree {
    param (
        [string]$FolderPath,
        [string]$Indent = ""
    )

    $getChildParams = @{
        Path        = $FolderPath
        Force       = $ShowHidden.IsPresent
        ErrorAction = "SilentlyContinue"
    }

    # Folders first, then files — both sorted alphabetically
    $items = Get-ChildItem @getChildParams |
        Sort-Object @{ Expression = { $_.PSIsContainer }; Descending = $true }, Name

    for ($i = 0; $i -lt $items.Count; $i++) {
        $item   = $items[$i]
        $isLast = ($i -eq $items.Count - 1)

        $branch      = if ($isLast) { $BRANCH_LAST } else { $BRANCH_MID }
        $childIndent = if ($isLast) { "$Indent$BLANK" } else { "$Indent$PIPE" }

        if ($item.PSIsContainer) {
            Write-Host "$Indent$branch" -NoNewline
            Write-Host "$($item.Name)/" -ForegroundColor Cyan
            Show-Tree -FolderPath $item.FullName -Indent $childIndent
        } else {
            Write-Host "$Indent$branch" -NoNewline
            Write-Host $item.Name -ForegroundColor White
        }
    }
}

# --- Entry point -------------------------------------------------------------

$resolvedPath = Resolve-Path -Path $Path -ErrorAction SilentlyContinue

if (-not $resolvedPath) {
    Write-Error "Path not found: $Path"
    exit 1
}

$rootName = Split-Path -Leaf $resolvedPath.Path
Write-Host "$rootName/" -ForegroundColor Yellow

Show-Tree -FolderPath $resolvedPath.Path
