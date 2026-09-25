# Guide: Adding More Questions

## 📊 Current Status

You currently have **61 questions**:
- Core Concepts: 25 questions
- Language Features: 20 questions
- Collections: 8 questions
- Advanced: 7 questions
- Concurrency: 1 question

By C++ version:
- Core (version-agnostic fundamentals): 37 questions
- C++11: 10 questions
- C++14: 3 questions
- C++17: 5 questions
- C++20: 4 questions
- C++23: 2 questions

**Target:** Add 100+ more questions (total ~160+)

---

## 🎯 Best Resources for C++ Questions

### 1. **cppreference.com** ⭐ (Most Reliable)
- **Language reference**: https://en.cppreference.com/w/cpp/language
- **Standard Library reference**: https://en.cppreference.com/w/cpp/header
- **Compiler support tables** (which version added what): https://en.cppreference.com/w/cpp/compiler_support

**Why:** Community-maintained but extremely accurate, comprehensive, and precise about which standard version introduced each feature.

### 2. **isocpp.org / WG21 Papers** ⭐ (Most Authoritative)
- **ISO C++ website**: https://isocpp.org/
- **WG21 papers (proposals for each standard)**: https://www.open-std.org/jtc1/sc22/wg21/docs/papers/
- **C++ FAQ**: https://isocpp.org/faq

**Why:** Straight from the standards committee. Best for understanding *why* a feature was added and its exact rationale.

### 3. **Learncpp.com** ⭐ (Excellent Tutorials)
- https://www.learncpp.com/

**Why:** Well-structured, beginner-friendly, thorough coverage of core language fundamentals (pointers, references, OOP, templates).

### 4. **C++ Core Guidelines**
- https://isocpp.github.io/CppCoreGuidelines/CppCoreGuidelines

**Why:** Great source for "best practice" style questions (RAII, resource management, modern idioms vs. old C-style patterns).

### 5. **Compiler Explorer (godbolt.org)**
- https://godbolt.org/

**Why:** Verify that a code example actually compiles and behaves as expected on a given standard version before writing a question about it.

### 6. **Stack Overflow**
- Search: "C++17 new features"
- Search: "C++20 concepts explained"

**Why:** Practical scenarios, edge cases, common misunderstandings.

### 7. **Books**
- "The C++ Programming Language" by Bjarne Stroustrup
- "Effective Modern C++" by Scott Meyers
- "C++ Primer" by Lippman, Lajoie, Moo

---

## 🤖 AI Limitations & Accuracy

### **My Accuracy Estimate: 70-85%**

**What I'm Good At:**
- ✅ Generating question structure and format
- ✅ Creating plausible multiple-choice options
- ✅ Writing clear explanations
- ✅ Understanding C++ syntax and concepts
- ✅ Formatting JSON correctly

**What I Struggle With:**
- ❌ **Version-specific details** - I might confuse which feature came in which standard (e.g. C++20 vs C++23)
- ❌ **Edge cases** - Subtle behavior differences (e.g. exact rules around implicit conversions, overload resolution)
- ❌ **Recent changes** - My training data might not include the latest C++23/26 details or defect reports
- ❌ **Technical precision** - Exact standard library signatures, header names, undefined-behavior nuances

### **Risk Areas:**
1. **Version Attribution** (30% error risk)
   - Example: I might say a feature is in C++17 when it's actually C++20
   - **Solution:** Always verify against cppreference.com's compiler support tables

2. **API Details** (20% error risk)
   - Header names, function signatures, return types
   - **Solution:** Check cppreference.com

3. **Behavioral Nuances / Undefined Behavior** (15% error risk)
   - How features interact, edge cases, UB
   - **Solution:** Test code examples on godbolt.org

### **Recommended Approach:**

#### Option 1: **Hybrid Approach** (Best)
1. **You provide topics/features** from official docs
2. **I generate questions** in the correct format
3. **You verify** against official sources
4. **You correct** any mistakes

#### Option 2: **I Generate, You Verify**
1. I generate a batch of questions
2. You review and verify each one
3. You correct version numbers, API details
4. You test edge cases on godbolt.org

#### Option 3: **You Write, I Format**
1. You write questions from official sources
2. I format them into JSON structure
3. I ensure consistency

---

## 📝 Question Generation Strategy

### Focus Areas for 100+ More Questions:

#### Core C++ (Target: 20+ more)
- Templates (function templates, class templates, specialization)
- Exception handling (try/catch, exception hierarchies, noexcept)
- The preprocessor (#define, macros, include guards vs #pragma once)
- Casts (static_cast, dynamic_cast, const_cast, reinterpret_cast)
- Undefined behavior and common pitfalls

#### C++11 (Target: 15+ more)
- std::thread and the memory model
- std::function and std::bind
- Delegating and inherited constructors
- Uniform initialization / initializer lists
- decltype

#### C++14 (Target: 10+ more)
- Variable templates
- std::exchange
- [[deprecated]] attribute (technically C++14)

#### C++17 (Target: 15+ more)
- std::filesystem
- Parallel algorithms (execution policies)
- Guaranteed copy elision
- Nested namespace definitions (namespace A::B { ... })
- constexpr if in more depth

#### C++20 (Target: 20+ more)
- Modules (import/export)
- std::span
- Designated initializers
- consteval and constinit
- std::jthread

#### C++23 (Target: 10+ more)
- std::print / std::println
- Multidimensional subscript operator (operator[](args...))
- std::stacktrace
- if consteval

---

## 🛠️ How to Add Questions

### Method 1: Add to JSON File

Edit `app/src/main/assets/questions.json`:

```json
{
  "id": 62,
  "questionText": "Your question here?",
  "options": [
    "Option 1",
    "Option 2",
    "Option 3",
    "Option 4"
  ],
  "correctAnswerIndex": 0,
  "explanation": "Detailed explanation here.",
  "languageVersion": "17",
  "category": "Language Features"
}
```

**Important:**
- IDs must be unique and sequential
- `correctAnswerIndex` is 0-based (0, 1, 2, or 3)
- `languageVersion` should be "11", "14", "17", "20", "23", or "Core"
- `category` should be one of: "Core Concepts", "Language Features", "Collections", "Advanced", "Concurrency", "APIs", or "General" — these match the fixed filter buttons in `CategorySelectionActivity.kt`; using any other string means the question is reachable only via "All Questions"

### Method 2: Add to QuestionBank.kt

Edit `app/src/main/java/com/cppquiz/app/data/QuestionBank.kt`:

```kotlin
Question(
    id = 62,
    questionText = "Your question here?",
    options = listOf(
        "Option 1",
        "Option 2",
        "Option 3",
        "Option 4"
    ),
    correctAnswerIndex = 0,
    explanation = "Detailed explanation here.",
    languageVersion = "17",
    category = "Language Features"
),
```

**Note:** The app loads from `questions.json` first, then falls back to `QuestionBank.kt` if the JSON fails to load or parse. Keep both in sync if you want the fallback path to have the same content.

---

## ✅ Verification Checklist

Before adding a question, verify:

- [ ] **Version is correct** - Feature actually exists in that C++ standard (check cppreference.com's compiler support page)
- [ ] **API/syntax is accurate** - Check cppreference.com, don't rely on memory
- [ ] **Explanation is correct** - Test with a real compiler (e.g. godbolt.org) if possible
- [ ] **Options are plausible** - Wrong answers should be believable, ideally common misconceptions
- [ ] **No typos** - Especially in code examples (C++ syntax is unforgiving)
- [ ] **Format is valid JSON** - Use a JSON validator
- [ ] **Category matches an existing filter** - see the list in Method 1 above

---

## 🚀 Quick Start: Let Me Generate Questions

If you want me to generate questions, I recommend:

1. **Start with 20-30 questions** (one batch)
2. **You review and verify** them
3. **I correct any mistakes** you find
4. **Repeat** until we have 160+

This iterative approach ensures quality.

### What I Need From You:

**Option A:** Just say "generate 20 C++20 questions" and I'll create them
- You'll need to verify version numbers and technical details

**Option B:** Provide specific topics, e.g.:
- "Generate 10 questions about concepts in C++20"
- "Generate 10 questions about move semantics in C++11"
- I'll be more accurate with specific topics

**Option C:** Provide source material:
- "Generate questions from this cppreference page: [URL]"
- I'll extract key points and create questions

---

## 📚 Example: Good Question Structure

```json
{
  "id": 62,
  "questionText": "What is the main advantage of std::unique_ptr over a raw owning pointer?",
  "options": [
    "It supports shared ownership across multiple objects",
    "It automatically deletes the owned object when it goes out of scope, preventing leaks",
    "It is faster than any raw pointer at runtime",
    "It can be implicitly converted to any other pointer type"
  ],
  "correctAnswerIndex": 1,
  "explanation": "std::unique_ptr applies RAII to heap ownership: when it goes out of scope, its destructor automatically deletes the object it owns, eliminating a whole class of manual delete-related bugs and leaks.",
  "languageVersion": "11",
  "category": "Advanced"
}
```

**Why this is good:**
- ✅ Clear, specific question
- ✅ Correct answer is accurate
- ✅ Wrong answers are plausible (shared ownership is std::shared_ptr, not unique_ptr)
- ✅ Explanation is helpful
- ✅ Version is correct (unique_ptr was introduced in C++11)

---

## 🎯 Recommended Action Plan

1. **Decide on approach:**
   - [ ] I generate, you verify
   - [ ] You provide topics, I generate
   - [ ] Hybrid approach

2. **Start small:**
   - Generate 20-30 questions first
   - Review and correct
   - Establish quality baseline

3. **Scale up:**
   - Once quality is confirmed, generate in batches
   - Focus on one C++ standard (or one category) at a time

4. **Final review:**
   - Verify all version numbers against cppreference.com
   - Test any code examples on godbolt.org
   - Check for duplicates

---

## 💡 Pro Tips

1. **Use cppreference.com's compiler support tables** - Most accurate source for "which version added this"
2. **Test code examples on godbolt.org** - Confirm behavior before writing the explanation
3. **Check multiple sources** - Cross-reference for accuracy, especially around undefined behavior
4. **Focus on practical scenarios** - Real-world usage (RAII, smart pointers, STL) is more valuable than trivia
5. **Include edge cases** - Makes questions more challenging (e.g. move-from state, iterator invalidation)

---

**Ready to start?** Tell me:
- How many questions you want initially (recommend 20-30)
- Which C++ standard to focus on first
- Any specific topics/features you want covered

I'll generate them, and you can verify against official sources!
