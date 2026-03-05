# Dangerous SF CLI Tools

> **This is a demo project.** It demonstrates how to wrap arbitrary CLI commands as MCP (Model Context Protocol) tools, making them available to AI clients such as Claude Desktop, Windsurf, and others.

## The Point

This is mainly for demo purposes, and works best if you already have the official SF CLI MCP installed.

See https://github.com/salesforcecli/mcp

The official MCP only gives you read only operations (probably a good thing). This MCP adds the
sf bulk delete operation as well. You also need to sf installed locally and you need to be 
authenticated to an Org. Probably the official MCP can do that for you (I didn't test it).

## What's Inside

| File | Purpose |
|------|---------|
| `DeleteRecordsTool.groovy` | The MCP tool — wraps `sf data delete bulk` and exposes it with typed parameters |
| `Application.groovy` | Spring Boot entry point; registers the tool with the MCP server |
| `application.properties` | Configures STDIO transport for MCP |
| `logback.xml` | Sends all logging to a file (STDIO MCP requires a clean stdout) |

## Tech Stack

- **Groovy 5** on **Java 24**
- **Spring Boot 3.4**
- **Spring AI MCP Server** (STDIO transport)
- **Salesforce CLI** (`sf`) — must be installed on the host machine

## The Tool: `sf_data_delete_bulk`

Bulk-deletes Salesforce records using a CSV file of record IDs. Maps directly to `sf data delete bulk`.

| Parameter | Required | Description |
|-----------|----------|-------------|
| `file` | Yes | Path to a CSV file with a single `Id` column |
| `sobject` | Yes | Salesforce object API name (e.g. `Account`, `MyObject__c`) |
| `targetOrg` | No | Username or alias of the target org |
| `wait` | No | Minutes to wait for completion (default: 0) |
| `hardDelete` | No | Bypass the Recycle Bin |
| `lineEnding` | No | `CRLF` or `LF` |
| `apiVersion` | No | Override the Salesforce API version |

## Prerequisites

- **Java 24+**
- **Salesforce CLI** (`sf`) installed and authenticated (`sf org login`)

## Build

```bash
./gradlew build -x test
```

The fat jar is produced at `build/libs/dangerous-sf-cli-tools-1.0-SNAPSHOT.jar`.

## Configure Your AI Client

Add the server to your MCP client config. For example, in Claude Desktop's `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "dangerous-sf-cli-tools": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/dangerous-sf-cli-tools-1.0-SNAPSHOT.jar"
      ]
    }
  }
}
```

Then ask Claude something like:

> *"Delete all Accounts that have "TEST" in the name"*

## Why "Dangerous"?

Because giving an AI agent the ability to run destructive CLI commands (like bulk-deleting records) is inherently risky. This project exists to show that it's *possible*, not that it's always *advisable*. Use responsibly.
