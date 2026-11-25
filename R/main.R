suppressPackageStartupMessages({
    library(dplyr)
    library(readr)
})
options(pillar.width = Inf)

print_log <- function(prompt, value){
    cat(sprintf(prompt, value))
}

load_data <- function(path) {
    df <- read_csv(path, 
                    col_types = cols(.default = col_character()),
                    show_col_types = FALSE)
    print_log("Total Rows Loaded: %d\n", nrow(df))

    return (df)
}

clean_data <- function(df){
    df  <- df %>%
        mutate(currency = toupper(trimws(currency)), type = tolower(trimws(type)),
                account_name = trimws(account_name)) %>%
        filter(currency == "PHP")
    
    print_log("Rows after filtering to PHP: %d\n", nrow(df))

    df <- df %>%
        mutate(date = as.Date(trimws(date), format = "%d/%m/%Y"),
                amount = suppressWarnings(as.numeric(gsub(",", "", trimws(amount)))),
                current_balance = suppressWarnings(as.numeric(gsub(",", "", trimws(current_balance))))
                )

    invalid_dates <- sum(is.na(df$date))
    invalid_amt <- sum(is.na(df$amount))
    invalid_curr_bal <- sum(is.na(df$current_balance))
    invalid_type <- sum(!(df$type %in% c("deposit", "withdrawal")))

    print_log("Total rows with invalid date: %d\n", invalid_dates)
    print_log("Total rows with invalid amount: %d\n", invalid_amt)
    print_log("Total rows with invalid current balance: %d\n", invalid_curr_bal)
    print_log("Total rows with invalid type: %d\n", invalid_type)

    df <- df %>% 
        filter(!is.na(account_name), !is.na(account_number), !is.na(date), !is.na(amount),
                !is.na(current_balance), type %in% c("deposit", "withdrawal"))

    print_log("Total rows after cleaning: %d\n", nrow(df))

    return (df)
}

data_frame <- clean_data(load_data("test2.csv"))
head(data_frame)

range(data_frame$date, na.rm = TRUE)



