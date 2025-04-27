/* Lib imports */
import { JSX, ReactNode } from "react";

type ProviderType = ({children}: {children: ReactNode}) => JSX.Element;

const AddProvider = ({rest, children}: {children: ReactNode, rest: ProviderType[]}) => {
  if (!rest.length) return <>{children}</>
  const First = rest[0];
  return <First><AddProvider rest={rest.slice(1)} children={children}/></First>
}

export function createProviders(...providers: ProviderType[]) {
  return ({children}: {children: ReactNode}) => {
    return <AddProvider rest={providers} children={children}/>
  }
}
