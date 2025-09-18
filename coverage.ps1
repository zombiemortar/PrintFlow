$ErrorActionPreference = 'Stop'

# Paths
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ProjectRoot
$LibDir = Join-Path $ProjectRoot 'lib'
$OutDir = Join-Path $ProjectRoot 'out'
$ClassesDir = Join-Path $OutDir 'classes'
$CoverageDir = Join-Path $ProjectRoot 'coverage'
$AgentJar = Join-Path $LibDir 'jacocoagent-0.8.12.jar'
$CliJar = Join-Path $LibDir 'jacococli-0.8.12.jar'
$JUnitJar = Join-Path $LibDir 'junit-platform-console-standalone-1.10.2.jar'
$ExecFile = Join-Path $CoverageDir 'jacoco.exec'

# Relative paths to avoid spaces issues with JVM arguments
$AgentRel = 'lib/jacocoagent-0.8.12.jar'
$CliRel = 'lib/jacococli-0.8.12.jar'
$JUnitRel = 'lib/junit-platform-console-standalone-1.10.2.jar'
$ClassesRel = 'out/classes'
$ExecRel = 'coverage/jacoco.exec'

# Ensure directories exist
New-Item -ItemType Directory -Force -Path $LibDir | Out-Null
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null
New-Item -ItemType Directory -Force -Path $ClassesDir | Out-Null
New-Item -ItemType Directory -Force -Path $CoverageDir | Out-Null

# Ensure JaCoCo jars exist
if (-not (Test-Path $AgentJar)) {
    Write-Host 'Downloading JaCoCo agent...'
    Invoke-WebRequest -UseBasicParsing -Uri 'https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.12/org.jacoco.agent-0.8.12-runtime.jar' -OutFile $AgentJar
}
if (-not (Test-Path $CliJar)) {
    Write-Host 'Downloading JaCoCo CLI...'
    Invoke-WebRequest -UseBasicParsing -Uri 'https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.12/org.jacoco.cli-0.8.12-nodeps.jar' -OutFile $CliJar
}

# Compile all Java sources
Write-Host 'Compiling Java sources...'
$javaFiles = Get-ChildItem -Path $ProjectRoot -Filter *.java -File | ForEach-Object { $_.FullName }
if ($javaFiles.Count -eq 0) { throw 'No .java files found.' }

# Include JUnit jar on classpath so tests compile
javac -cp $JUnitRel -d $ClassesRel @($javaFiles | ForEach-Object { '"' + $_ + '"' })

# Run tests with JaCoCo agent
Write-Host 'Running tests with JaCoCo agent...'
if (Test-Path $ExecFile) { Remove-Item -Force $ExecFile }
java -javaagent:$AgentRel=destfile=$ExecRel,output=file -jar $JUnitRel --class-path $ClassesRel --scan-class-path | Write-Host

# Generate coverage reports
Write-Host 'Generating JaCoCo reports (HTML, XML, CSV)...'
java -jar $CliRel report $ExecRel --classfiles $ClassesRel --sourcefiles . --html coverage/html --xml coverage/jacoco.xml --csv coverage/jacoco.csv | Write-Host

Write-Host "Done. Open: $($CoverageDir)\html\index.html"


