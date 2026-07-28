import re

with open('app/src/main/java/com/example/english_app/ui/HomeScreen.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add BackHandler import if missing
if 'import androidx.activity.compose.BackHandler' not in content:
    content = content.replace('import androidx.compose.foundation.layout.*', 'import androidx.activity.compose.BackHandler\nimport androidx.compose.foundation.layout.*')

# 2. Add currentFolder state
if 'var currentFolder by remember' not in content:
    content = content.replace('var searchQuery by remember { mutableStateOf(\"\") }', 'var searchQuery by remember { mutableStateOf(\"\") }\n    var currentFolder by remember { mutableStateOf<String?>(null) }\n\n    BackHandler(enabled = currentFolder != null) {\n        currentFolder = null\n    }')

# 3. Update TopAppBar
top_bar_old = '''TopAppBar(
                    title = {
                        Text(
                            text = if (searchQuery.isNotEmpty()) \"Search: \\" else \"Learning\",
                            fontWeight = FontWeight.Bold,
                            color = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = \"Menu\", tint = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White)
                        }
                    },'''

top_bar_new = '''TopAppBar(
                    title = {
                        val titleText = when {
                            searchQuery.isNotEmpty() -> \"Search: \\"
                            currentFolder != null -> currentFolder!!
                            else -> \"Learning\"
                        }
                        Text(
                            text = titleText,
                            fontWeight = FontWeight.Bold,
                            color = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    },
                    navigationIcon = {
                        if (currentFolder != null) {
                            IconButton(onClick = { currentFolder = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = \"Back\", tint = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White)
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = \"Menu\", tint = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White)
                            }
                        }
                    },'''
content = content.replace(top_bar_old, top_bar_new)

# 4. Replace Categories block
old_categories_block = '''val filteredCategories = categories.filter { it.title.contains(searchQuery, ignoreCase = true) }
                if (filteredCategories.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredCategories) { category ->
                            EnhancedCategoryCard(
                                category = category,
                                onCategorySelected = onCategorySelected,
                                onSpeakCategory = { 
                                    tts.speak(category.title, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            )
                        }
                    }
                } else {'''

new_categories_block = '''val filteredCategories = categories.filter { it.title.contains(searchQuery, ignoreCase = true) }
                if (filteredCategories.isNotEmpty()) {
                    if (currentFolder == null) {
                        val sampleLearnCategory = Category(
                            id = "sample_learn",
                            title = "Sample Learn",
                            description = "Sample learning modules",
                            color = MaterialTheme.colorScheme.primary,
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            words = emptyList()
                        )
                        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            item {
                                GridCategoryCard(
                                    category = sampleLearnCategory,
                                    onCategorySelected = { currentFolder = "Sample Learn" }
                                )
                            }
                        }
                    } else {
                        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            items(filteredCategories.size) { index ->
                                val category = filteredCategories[index]
                                GridCategoryCard(
                                    category = category,
                                    onCategorySelected = onCategorySelected
                                )
                            }
                        }
                    }
                } else {'''

content = content.replace(old_categories_block, new_categories_block)

# 5. Add GridCategoryCard at the end
grid_card_code = '''
// Helper to abbreviate category names (e.g., "Basic Vocabulary" -> "B. V.")
fun abbreviateCategoryTitle(title: String): String {
    val words = title.split(" ")
    if (words.size == 1) return title
    return words.filter { it.isNotBlank() }
                .joinToString(" ") { it.take(1).uppercase() + "." }
}

@Composable
fun GridCategoryCard(
    category: Category,
    onCategorySelected: (Category) -> Unit
) {
    val shortTitle = abbreviateCategoryTitle(category.title)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable { onCategorySelected(category) }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = category.color,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = category.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        )
    }
}
'''

if 'fun GridCategoryCard' not in content:
    content += grid_card_code

with open('app/src/main/java/com/example/english_app/ui/HomeScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated successfully")
