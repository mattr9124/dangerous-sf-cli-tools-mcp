package com.sf.ps.tools

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component

@Component
class DeleteRecordsTool {

    private static final Logger log = LoggerFactory.getLogger(DeleteRecordsTool)

    @Tool(name = "sf_data_delete_bulk", description = "Bulk delete Salesforce records from an org using a CSV file containing record IDs. Uses Bulk API 2.0 via the Salesforce CLI. The CSV file must have only one column ('Id') followed by the list of record IDs to delete, one per line.")
    String deleteBulk(
            @ToolParam(description = "Absolute path to the CSV file containing the record IDs to delete. The CSV must have a single 'Id' column header followed by one record ID per line.", required = true)
            String file,
            @ToolParam(description = "API name of the Salesforce object (standard or custom) to delete records from, e.g. 'Account' or 'MyObject__c'.", required = true)
            String sobject,
            @ToolParam(description = "Username or alias of the target org. If omitted, the default target-org is used.", required = false)
            String targetOrg,
            @ToolParam(description = "Number of minutes to wait for the command to complete before displaying the results. Defaults to 0 (returns immediately).", required = false)
            Integer wait,
            @ToolParam(description = "If true, marks records as immediately eligible for deletion (bypasses Recycle Bin). Requires the 'Bulk API Hard Delete' system permission.", required = false)
            Boolean hardDelete,
            @ToolParam(description = "Line ending used in the CSV file: 'CRLF' or 'LF'. Defaults to LF on macOS/Linux, CRLF on Windows.", required = false)
            String lineEnding,
            @ToolParam(description = "Override the Salesforce API version used for this request, e.g. '59.0'.", required = false)
            String apiVersion
    ) {
        def command = ['sf', 'data', 'delete', 'bulk',
                                '--sobject', sobject,
                                '--file', file,
                                '--json']

        if (targetOrg) {
            command.addAll(['--target-org', targetOrg])
        }
        if (wait != null) {
            command.addAll(['--wait', wait.toString()])
        }
        if (hardDelete) {
            command.addAll(['--hard-delete'])
        }
        if (lineEnding) {
            command.addAll(['--line-ending', lineEnding])
        }
        if (apiVersion) {
            command.addAll(['--api-version', apiVersion])
        }

        log.info('Executing command: {}', command.join(' '))

        try {
            def pb = new ProcessBuilder(command)
            pb.redirectErrorStream true
            def process = pb.start()
            def output = process.inputStream.text
            def exitCode = process.waitFor()

            log.info('Command output (exit code {}):\n{}', exitCode, output)

            if (exitCode == 0) {
                return output
            }
            return "Command failed with exit code ${exitCode}:\n${output}"
        } catch (Exception e) {
            return "Error executing sf CLI: ${e.message}"
        }
    }
}
