package com.sf.ps

import com.sf.ps.tools.DeleteRecordsTool
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }

    @Bean
    ToolCallbackProvider dangerousSfTools(DeleteRecordsTool deleteRecordsTool) {
        MethodToolCallbackProvider.builder()
                .toolObjects(deleteRecordsTool)
                .build()
    }
}
