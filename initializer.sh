#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored messages
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Function to validate application ID format
validate_application_id() {
    local app_id=$1
    # Check if it matches the pattern: com.example.app (at least 2 segments)
    if [[ ! $app_id =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
        return 1
    fi
    return 0
}

# Function to validate application name
validate_application_name() {
    local app_name=$1
    # Check if it's not empty and contains only alphanumeric characters
    if [[ -z "$app_name" ]] || [[ ! $app_name =~ ^[a-zA-Z][a-zA-Z0-9]*$ ]]; then
        return 1
    fi
    return 0
}

# Print header
echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║        ComposeTemplate Project Initializer                ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Get and validate applicationId
while true; do
    print_info "Enter applicationId (e.g., com.example.myapp):"
    read applicationId
    
    if validate_application_id "$applicationId"; then
        print_success "Valid application ID: $applicationId"
        break
    else
        print_error "Invalid application ID format!"
        print_warning "Format should be: com.example.myapp (lowercase, at least 2 segments)"
        echo ""
    fi
done

echo ""

# Get and validate applicationName
while true; do
    print_info "Enter applicationName (e.g., MyApp):"
    read applicationName
    
    # Trim application name
    applicationName=$(echo $applicationName | tr -d '[:space:]')
    # Format application name (capitalize first letter of each word)
    applicationName=$(echo $applicationName | sed -e 's/\b\(.\)/\u\1/g')
    
    if validate_application_name "$applicationName"; then
        print_success "Valid application name: $applicationName"
        break
    else
        print_error "Invalid application name!"
        print_warning "Name should start with a letter and contain only alphanumeric characters"
        echo ""
    fi
done

echo ""

# Derive plugin ID prefix from application ID (e.g., com.example.myapp -> myapp)
pluginIdPrefix=$(echo $applicationId | awk -F. '{print $NF}')

# Confirm details
echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Configuration Summary                                     ║"
echo "╠════════════════════════════════════════════════════════════╣"
printf "║  Application ID:     %-37s ║\n" "$applicationId"
printf "║  Application Name:   %-37s ║\n" "$applicationName"
printf "║  Plugin ID Prefix:   %-37s ║\n" "$pluginIdPrefix"
printf "║  Target Directory:   %-37s ║\n" "../$applicationName"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

print_warning "This will create a new project at: ../$applicationName"
read -p "Continue? (y/n): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_error "Initialization cancelled."
    exit 1
fi

echo ""
print_info "Starting project initialization..."
echo ""

# Check if target directory already exists
if [ -d "../$applicationName" ]; then
    print_error "Directory ../$applicationName already exists!"
    exit 1
fi

# Copy the template to the applicationName directory

print_info "Copying template files..."

# Create target directory
if ! mkdir -p "../$applicationName"; then
    print_error "Failed to create directory ../$applicationName"
    exit 1
fi

# Use rsync to copy files while excluding build artifacts and git files
rsync -av \
    --exclude '.git' \
    --exclude '.idea' \
    --exclude '.gradle' \
    --exclude 'build' \
    --exclude 'bin' \
    --exclude 'local.properties' \
    --exclude "$applicationName" \
    . "../$applicationName" > /dev/null

if [ $? -ne 0 ]; then
    print_error "Failed to copy template files!"
    exit 1
fi
print_success "Template files copied"

# Remove initializer script from new project
rm "../$applicationName/initializer.sh"
rm "../$applicationName/.gitignore"

# Move app source directories
print_info "Restructuring app source directories..."

# Convert applicationId to directory path (e.g., com.example.app -> com/example/app)
appIdPath=$(echo $applicationId | tr '.' '/')

# Function to safely move files
safe_move() {
    local src_base=$1
    local old_pkg=$2
    local new_pkg=$3
    
    if [ -d "$src_base/$old_pkg" ]; then
        # Create temp dir
        local temp_dir="${src_base}_temp"
        mkdir -p "$temp_dir"
        
        # Move files to temp
        mv "$src_base/$old_pkg/"* "$temp_dir/"
        
        # Remove old com directory
        rm -rf "$src_base/com"
        
        # Create new directory structure
        mkdir -p "$src_base/$new_pkg"
        
        # Move files to new location
        mv "$temp_dir/"* "$src_base/$new_pkg/"
        
        # Remove temp dir
        rmdir "$temp_dir"
    fi
}

# Move main source files
safe_move "../$applicationName/app/src/main/java" "com/ytapps/composetemplate" "$appIdPath"

# Move androidTest files
safe_move "../$applicationName/app/src/androidTest/java" "com/ytapps/composetemplate" "$appIdPath"

# Move test files
safe_move "../$applicationName/app/src/test/java" "com/ytapps/composetemplate" "$appIdPath"

print_success "Source directories restructured"

# Move build-logic convention plugin source files
print_info "Restructuring build-logic convention plugins..."

# Create target directory for convention plugins
conventionPath=$(echo $applicationId | tr '.' '/')

# Move convention plugin files
safe_move "../$applicationName/build-logic/convention/src/main/kotlin" "com/ytapps/composetemplate/convention" "$conventionPath/convention"

print_success "Build-logic restructured"

# Replace package names and references
print_info "Updating package names and references..."

# Find and replace in all relevant files
find "../$applicationName" -type f \( -name "*.kt" -o -name "*.xml" -o -name "*.kts" -o -name "*.properties" -o -name "*.pro" \) -print0 | while IFS= read -r -d $'\0' file; do
    # Replace package name
    sed -i '' "s/com\.ytapps\.composetemplate/$applicationId/g" "$file"
    
    # Replace application name
    sed -i '' "s/ComposeTemplate/$applicationName/g" "$file"
    
    # Replace plugin ID prefix (e.g., composetemplate.android.* -> myapp.android.*)
    sed -i '' "s/composetemplate\.android\./$pluginIdPrefix.android./g" "$file"
done

print_success "Package names and references updated"

# Create new .gitignore file
print_info "Creating .gitignore file..."
cat > "../$applicationName/.gitignore" << 'EOF'
# Built application files
*.apk
*.aab
*.ap_
*.aab

# Files for the ART/Dalvik VM
*.dex

# Java class files
*.class

# Generated files
bin/
gen/
out/
release/

# Gradle files
.gradle/
build/
.cxx/
local.properties

# Local configuration file (sdk path, etc)
local.properties

# IntelliJ
*.iml
.idea/
misc.xml
deploymentTargetDropDown.xml
render.experimental.xml

# Keystore files
*.jks
*.keystore

# External native build folder generated in Android Studio 2.2 and later
.externalNativeBuild
.cxx/

# Google Services (e.g. APIs or Firebase)
google-services.json

# Android Profiling
*.hprof

# macOS
.DS_Store

# Kotlin
*.kotlin_module

# Backup files
*~
*.swp
*.bak

# Log Files
*.log
EOF

print_success ".gitignore created"

# Initialize git repository
print_info "Initializing git repository..."
cd "../$applicationName"
git init
git add .
git commit -m "Initial commit: $applicationName project from ComposeTemplate"
cd - > /dev/null

print_success "Git repository initialized"

# Print success message
echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║  🎉 Project Created Successfully!                          ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
print_success "Project name: $applicationName"
print_success "Location: ../$applicationName"
echo ""
print_info "Next steps:"
echo "  1. cd ../$applicationName"
echo "  2. Update gradle.properties with your API URLs:"
echo "     - BASE_URL_DEBUG=https://your-debug-api-url.com/"
echo "     - BASE_URL=https://your-production-api-url.com/"
echo "  3. Open the project in Android Studio"
echo "  4. Sync Gradle files"
echo "  5. Start building your app! 🚀"
echo ""