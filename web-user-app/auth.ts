import NextAuth from "next-auth"
import Google from "next-auth/providers/google"
import Apple from "next-auth/providers/apple"
import Credentials from "next-auth/providers/credentials"


export const { auth, handlers, signIn, signOut } = NextAuth({
  trustHost: true,
  providers: [

    Google({
      clientId: process.env.AUTH_GOOGLE_ID ?? "",
      clientSecret: process.env.AUTH_GOOGLE_SECRET ?? "",
    }),

    Apple({
      clientId: process.env.AUTH_APPLE_ID ?? "",
      clientSecret: process.env.AUTH_APPLE_SECRET ?? "",
    }),

    Credentials({
      name: "Email and Password",
      credentials: {
        email: { label: "Email", type: "email" },
        password: { label: "Password", type: "password" },
      },
      async authorize(credentials) {
        if (!credentials?.email || !credentials?.password) {
          return null
        }

        const apiBaseUrl = process.env.API_BASE_URL || process.env.NEXT_PUBLIC_API_BASE_URL
        if (!apiBaseUrl) {
          return null
        }

        try {
          const response = await fetch(`${apiBaseUrl}/api/v1/auth/login`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Accept: "application/json",
            },
            body: JSON.stringify({
              email: credentials.email,
              password: credentials.password,
            }),
          })

          if (!response.ok) {
            return null
          }

          const payload = await response.json()
          const user = payload.user ?? {}

          return {
            id: user.id ?? credentials.email,
            email: user.email ?? credentials.email,
            name: user.displayName ?? user.name ?? credentials.email,
            accessToken: payload.accessToken,
            refreshToken: payload.refreshToken,
          }
        } catch {
          return null
        }
      },
    }),

  ],
  pages: {
    signIn: "/login",
  },
  callbacks: {
    async jwt({ token, user, account }) {
      if (user) {
        token.userId = user.id
        token.accessToken = (user as { accessToken?: string }).accessToken
        token.refreshToken = (user as { refreshToken?: string }).refreshToken
      }

      if (account) {
        token.provider = account.provider
      }

      return token
    },
    async session({ session, token }) {
      if (session.user) {
        session.user.id = token.userId as string
        session.user.provider = token.provider as string | undefined
      }

      session.accessToken = token.accessToken as string | undefined
      session.refreshToken = token.refreshToken as string | undefined

      return session
    },
  },
  session: {
    strategy: "jwt",
  },
})
